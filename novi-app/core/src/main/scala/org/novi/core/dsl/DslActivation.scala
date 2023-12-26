package org.novi.core.dsl

import org.novi.core.activations.BaseActivation

trait DslActivation(var configuration: String) extends BaseActivation[String] {

  def &(that: DslActivation): AndActivation = AndActivation(this, that, s"( ${this.configuration} & ${that.configuration} )")

  def |(that: DslActivation): OrActivation = OrActivation(this, that, s"( ${this.configuration} | ${that.configuration} )")

  def unary_! : NotActivation = NotActivation(this, s"!(${this.configuration})")

  def apply(context: String): java.lang.Boolean

  override def configuration(configuration: String): BaseActivation[String] = {
    this.configuration = configuration
    this
  }
}
