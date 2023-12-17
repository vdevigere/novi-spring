--DROP
--  TABLE flag_activation_configs;
--DROP
--  TABLE flag;
--DROP
--  TABLE activation_config;
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