insert into flag
values
  (1, 'featureA'),
  (2, 'featureB'),
  (3, 'featureC'),
  (4, 'featureD'),
  (5, 'featureE'),
  (6, 'featureF');
insert into activation_config
values
  (
    1, '{"startDateTime":"11-12-2023 12:00","endDateTime":"20-12-2023 12:00" }',
    'DateTime', 'org.novi.activations.dsl.DateTimeActivation'
  ),
  (
    2, '{"SampleA":100.0,"SampleB":0,"SampleC":0}',
    'Always SAMPLE A', 'org.novi.activations.dsl.WeightedRandomActivation'
  ),
  (
    3, '{"activationIds":[1,2],"operation":"AND"}',
    '1 AND 2', 'org.novi.web.activations.ComboBooleanActivations'
  ),
 (
   4, '{"activationIds":[1,2],"operation":"OR"}',
   '1 OR 2', 'org.novi.web.activations.ComboBooleanActivations'
 ),
 (
    5, '{"activationIds":[6,7],"operation":"AND"}',
    '!False & (False | True)', 'org.novi.web.activations.ComboBooleanActivations'
  ),
 (
    6, '!org.novi.activations.dsl.FalseActivation("False-1") & (org.novi.activations.dsl.FalseActivation("False-2") | org.novi.activations.dsl.TrueActivation("True-3"))',
    'DSL', 'org.novi.web.activations.DslEvaluator'
 ),
 (
     7, 'org.novi.activations.dsl.FalseActivation("False-1") & (org.novi.activations.dsl.FalseActivation("False-2") | org.novi.activations.dsl.TrueActivation("True-3"))',
     'DSL', 'org.novi.web.activations.DslEvaluator'
  ),
  (
    8, 'does not matter', 'DSL activation used as non-dsl activation', 'org.novi.activations.dsl.DynamicActivation'
  );
insert into flag_activation_configs
values
  (1, 1),
  (1, 2),
  (2, 3),
  (3, 4),
  (4, 5),
  (5, 7),
  (6, 8);