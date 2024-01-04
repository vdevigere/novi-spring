package org.novi.activations.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DSLTest {
  @Test
  def testEvaluate1(): Unit = {
    val bEval = FalseActivation("False-1") | FalseActivation("False-2") & TrueActivation("True-3")
    assertThat(bEval.evaluate("World")).isFalse()
  }

  @Test
  def testEvaluate2(): Unit = {
    val bEval = FalseActivation("False-1") & FalseActivation("False-2") | TrueActivation("True-3")
    assertThat(bEval.evaluate("World")).isTrue()
  }

  @Test
  def testEvaluate3(): Unit = {
    val bEval = FalseActivation("False-1") & (FalseActivation("False-2") | TrueActivation("True-3"))
    assertThat(bEval.evaluate("World")).isFalse()
  }

  @Test
  def testEvaluate4(): Unit = {
    val bEval = !FalseActivation("False-1") & (FalseActivation("False-2") | TrueActivation("True-3"))
    assertThat(bEval.evaluate("World")).isTrue()
  }

  @Test
  def testEvaluate5(): Unit = {
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
    val bEval = WeightedRandomActivation(config) & TrueActivation("True-2")
    assertThat(bEval.evaluate(context)).isFalse
  }
}
