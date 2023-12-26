package org.novi.activations.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DSLTest {
  @Test
  def testEvaluate1(): Unit = {
    val bEval = FalseActivation("False-1") | FalseActivation("False-2") & TrueActivation("True-3")
    assertThat(bEval("World")).isFalse()
  }

  @Test
  def testEvaluate2(): Unit = {
    val bEval = FalseActivation("False-1") & FalseActivation("False-2") | TrueActivation("True-3")
    assertThat(bEval("World")).isTrue()
  }

  @Test
  def testEvaluate3(): Unit = {
    val bEval = FalseActivation("False-1") & (FalseActivation("False-2") | TrueActivation("True-3"))
    assertThat(bEval("World")).isFalse()
  }

  @Test
  def testEvaluate4(): Unit = {
    val bEval = !FalseActivation("False-1") & (FalseActivation("False-2") | TrueActivation("True-3"))
    assertThat(bEval("World")).isTrue()
  }
}
