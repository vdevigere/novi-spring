package org.novi.activations.dsl

import org.novi.core.activations.{BaseActivation, BaseActivationFactory}
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

class FalseActivation(configuration: String) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[FalseActivation])
  override def evaluate(context: String): java.lang.Boolean = {
    logger.debug("Always returning false: {}", context)
    false
  }
  override def apply(s: String): BaseActivation[String] = FalseActivation.apply(s)
}

object FalseActivation extends BaseActivationFactory[String]{

  override def apply(configuration: String): BaseActivation[String] = new FalseActivation(configuration)
}