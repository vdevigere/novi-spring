package org.novi.activations.dsl

import org.novi.core.activations.{BaseActivation, BaseActivationFactory}
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

import java.lang

class DynamicActivation(configuration: String = null) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[DynamicActivation])

  // No-Arg constructor required to instantiate via ServiceLoader
  def this() = {
    this(null)
  }

  override def evaluate(context: String): lang.Boolean = {
    logger.debug("Context:{}", context)
    java.lang.Boolean.valueOf(context)
  }

  override def apply(configuration: String): BaseActivation[String] = DynamicActivation.apply(configuration)
}

object DynamicActivation extends BaseActivationFactory[String] {
  override def apply(configuration: String): DynamicActivation = new DynamicActivation(configuration)
}
