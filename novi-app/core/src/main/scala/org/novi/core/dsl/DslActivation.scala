package org.novi.core.dsl

import org.novi.core.activations.BaseActivation

trait DslActivation[T](var configuration: T) extends BaseActivation[T] {

  def &(that: DslActivation[?]): AndActivation = AndActivation(this, that, s"( ${this.configuration} & ${that.configuration} )")

  def |(that: DslActivation[?]): OrActivation = OrActivation(this, that, s"( ${this.configuration} | ${that.configuration} )")

  def unary_! : NotActivation = NotActivation(this, s"!(${this.configuration})")

  def evaluate(context: String): java.lang.Boolean
}
