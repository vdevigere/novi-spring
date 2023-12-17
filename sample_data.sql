DROP TABLE flag_activation_configs;
DROP TABLE flag;
DROP TABLE activation_config;

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
	CONSTRAINT uk_p46m1gwd6pehekyetq550xssl UNIQUE (name)
);

CREATE TABLE flag_activation_configs (
	flag_id int8 NOT NULL,
	activation_configs_id int8 NOT NULL,
	CONSTRAINT flag_activation_configs_pkey PRIMARY KEY (flag_id, activation_configs_id)
);

ALTER TABLE public.flag_activation_configs ADD CONSTRAINT fk1x0i05y2dn7tmu7fbjhe99dfp FOREIGN KEY (activation_configs_id) REFERENCES activation_config(id);
ALTER TABLE public.flag_activation_configs ADD CONSTRAINT fkm9qflt1v1b73ex27h9kwab1yv FOREIGN KEY (flag_id) REFERENCES flag(id);

insert into flag values(1, 'featureA'),(2, 'featureB');
insert into activation_config values (1, '{"startDateTime":"11-12-2023 12:00","endDateTime":"20-12-2023 12:00" }', 'DateTime','org.novi.activations.DateTimeActivation'),
(2, '{"SampleA":100.0,"SampleB":0,"SampleC":0}','Always SAMPLE A','org.novi.activations.WeightedRandomActivation'),
(3, '{"activationIds":[1,2],"operation":"AND"}','1 AND 2','org.novi.web.activations.ComboBooleanActivations');
insert into flag_activation_configs values(1,1),(1,2),(2,3);
