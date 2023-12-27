package org.novi.activations.dsl

import org.apache.commons.math3.util.Pair
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.novi.core.exceptions.{ConfigurationParseException, ContextParseException}

class WeightedRandomActivationTest {
  @Test
  @throws[ConfigurationParseException]
  private[activations] def parseConfiguration(): Unit = {
    val wra = new WeightedRandomActivation
    val config =
      """
                {
                "SampleA":50.0,
                "SampleB":25.0,
                "SampleC":25.0
                }
                """
    val retVal: WeightedRandomActivation = wra.valueOf(config).asInstanceOf[WeightedRandomActivation]
    assertThat(retVal.variant_and_weights).contains(Pair.create("SampleA", 50.0), Pair.create("SampleB", 25.0), Pair.create("SampleC", 25.0))
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def testEvaluateToTrue(): Unit = {
    val wra = new WeightedRandomActivation
    val config =
      """
                {
                "SampleA":100.0,
                "SampleB":0,
                "SampleC":0
                }
                """
    val context =
      """
                {
                    "org.novi.activations.dsl.WeightedRandomActivation":{
                        "seed": 200,
                        "variantToCheck": "SampleA"
                    }
                }
                """
    assertThat(wra.valueOf(config).apply(context)).isTrue
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def testEvaluateToFalse(): Unit = {
    val wra = new WeightedRandomActivation
    val config =
      """
                {
                "SampleA":0.0,
                "SampleB":100,
                "SampleC":0
                }
                """
    val context =
      """
                {
                    "org.novi.activations.dsl.WeightedRandomActivation":{
                        "seed": 200,
                        "variantToCheck": "SampleA"
                    }
                }
                """
    assertThat(wra.valueOf(config).apply(context)).isFalse
  }
}
