insert into flag
values
  (1, 'featureA'),
  (2, 'featureB'),
  (3, 'featureC');
insert into activation_config
values
  (
    1, '{"startDateTime":"11-12-2023 12:00","endDateTime":"20-12-2023 12:00" }',
    'DateTime', 'org.novi.activations.DateTimeActivation'
  ),
  (
    2, '{"SampleA":100.0,"SampleB":0,"SampleC":0}',
    'Always SAMPLE A', 'org.novi.activations.WeightedRandomActivation'
  ),
  (
    3, '{"activationIds":[1,2],"operation":"AND"}',
    '1 AND 2', 'org.novi.web.activations.ComboBooleanActivations'
  ),
 (
   4, '{"activationIds":[1,2],"operation":"OR"}',
   '1 OR 2', 'org.novi.web.activations.ComboBooleanActivations'
 );
insert into flag_activation_configs
values
  (1, 1),
  (1, 2),
  (2, 3),
  (3, 4);