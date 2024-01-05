package org.novi.activations.dsl

import org.novi.core.activations.{BaseActivation, BaseActivationFactory}
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

class TrueActivation(configuration: String) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[TrueActivation])

  override def evaluate(context: String): java.lang.Boolean = {
    logger.debug("Always returning true: {}", context)
    true
  }

  override def apply(s: String): BaseActivation[String] = TrueActivation.apply(s)
}

object TrueActivation extends BaseActivationFactory[String] {

  override def apply(configuration: String): TrueActivation = new TrueActivation(configuration)
}