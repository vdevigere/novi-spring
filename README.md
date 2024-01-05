# Novi-Spring

novi-spring is the spring boot based implementation of [Novi](https://github.com/vdevigere/Novi): A dynamic feature flag
platform.
The architecture and APIs are more or less similar.

## Usage

If you have docker, run `docker compose up` in the parent directory, which executes the following steps

- The script downloads the postgresql image
- and builds the code and executes it while connecting to the postgres instance.
- Creates tables and sample data from the db-init-scripts folder is inserted into the tables

If you make code changes, run `docker compose build web` or (`docker compose build web --no-cache` to force recreate
image) to rebuild the image and then run `docker compose up`

The project is a mixed Scala and Java based project. Scala is used to support expressing the activation logic using a
DSL. The REST endpoints and JPA based data retrieval
is built in Spring-Boot/Java. I mainly started this project to refresh my Java and Scala skills.

The backend persistence layer of Novi is comprised of 3 tables which capture the details related to the two main
entities:-

### Flags

Flags or Feature flags turn on or off features. Each flag is typically associated with one feature and its status is
determined by a set of Activation rules
A single feature flag can be associated with multiple activation rules.

### Activation Rules

Activation Rules determine the conditions that either turn on or off feature flags. An activation rule can be associated
with multiple feature flags.

The DDL scripts for the tables required to capture the Many-To-Many relationship between Flags and Activations
is [ddl.sql](/db-init-scripts/ddl.sql)

```sql
CREATE TABLE activation_config (
  id int8 NOT NULL,
  config text NULL,
  description text NULL,
  "name" varchar(255) NULL,
  CONSTRAINT activation_config_pkey PRIMARY KEY (id)
);
CREATE TABLE flag (
  id int8 NOT NULL,
  "name" varchar(255) NULL,
  CONSTRAINT flag_pkey PRIMARY KEY (id),
  CONSTRAINT unique_name UNIQUE (name)
);
CREATE TABLE flag_activation_configs (
  flag_id int8 NOT NULL,
  activation_configs_id int8 NOT NULL,
  CONSTRAINT flag_activation_configs_pkey PRIMARY KEY (flag_id, activation_configs_id)
);
ALTER TABLE
  public.flag_activation_configs
ADD
  CONSTRAINT fk_activation_config_id FOREIGN KEY (activation_configs_id) REFERENCES activation_config(id);
ALTER TABLE
  public.flag_activation_configs
ADD
  CONSTRAINT fk_flag_id FOREIGN KEY (flag_id) REFERENCES flag(id);
```

If a feature flag is associated with multiple activation rules, each rule is evaluated individually and then "AND"ed
with each other.
If further flexibility is required, users can express the activation rules in the form of a DSL eg:-

```
DateBasedActivation(2) & WeightedRandomActivation(4)
```

The numbers in parenthesis represent the database Ids of the associated configuration (retrieved from the
activation_config table) eg:- The configuration for a
DateBasedActivation rule may specify the start date and the end date for when the rule should be activated.

```json
{
  "startDateTime": "11-12-2023 12:00",
  "endDateTime": "20-12-2023 12:00"
}
```

Similarly the configuration for
a WeightedRandomActivation rule may specify the different variations and weights which influence the random selection of
a variant

```json
{
  "SampleA": 50.0,
  "SampleB": 25.0,
  "SampleC": 25.0
}
```

The DSL is then evaluated against the current context to determine the status. The context could be provided as a JSON
payload such as:-

```json
{
  "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "15-12-2023 12:00",
  "org.novi.activations.dsl.WeightedRandomActivation": {
    "seed": 200,
    "variantToCheck": "SampleA"
  }
}
```

Given the above context payload, The DateBasedActivation rule is evaluated as True (12/15 is between 12/11 and 12/20)
There is a 50% chance that SampleA is True and if it is then the DSL (True & True) is True and the flag and hence the
feature is turned on.

The DSL is Scala code that is loaded and evaluated at runtime and can be slow, the alternate approach which is much
faster is to evaluate implicitly.

Flags

| id | Name      |
|----|-----------|
| 1  | Feature-A |

Activations

| id | config                                                                 | description     | name                                              |
|----|------------------------------------------------------------------------|-----------------|---------------------------------------------------|
| 1  | {"startDateTime":"11-12-2023 12:00","endDateTime":"20-12-2023 12:00" } | DateTime        | org.novi.activations.dsl.DateTimeActivation       |
| 2  | {"SampleA":100.0,"SampleB":0,"SampleC":0}                              | Always SAMPLE A | org.novi.activations.dsl.WeightedRandomActivation |

The conditions are expressed as database rows and given the association

| flag_id | activation_configs_id |
|---------|-----------------------|
| 1       | 1                     |
| 1       | 2                     |

The result of evaluating each row of activation config rules is implicitly "AND"ed with each other to give the same
result

There is also an in-built ```ComboBooleanActivations``` rule that lets you choose the operation to perform expressed in
a table as

| id | config                                    | description | name                                             |
|----|-------------------------------------------|-------------|--------------------------------------------------|
| 3  | {"activationIds":[1,2],"operation":"AND"} | 1 AND 2     | org.novi.web.activations.ComboBooleanActivations |
| 4  | {"activationIds":[1,2],"operation":"OR"}  | 1 OR 2      | org.novi.web.activations.ComboBooleanActivations |

## Custom Activations

Have a look
at [WeighteRandomActivation](novi-activations/src/main/scala/org/novi/activations/dsl/WeightedRandomActivation.scala) to
build your own activation logic
These can be packaged into a jar and dropped into the classpath for the platform to pick up. The docker container adds
the [plugin-activations](plugin-activations) folder to the classpath and any activation jars added here can be utilized
in either DSLs or implicit logic 
