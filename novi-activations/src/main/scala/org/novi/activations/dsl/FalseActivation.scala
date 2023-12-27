package org.novi.activations.dsl

import org.novi.core.activations.BaseActivation
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

class FalseActivation(configuration: String) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[FalseActivation])
  override def apply(context: String): java.lang.Boolean = {
    logger.debug("Always returning false: {}", context)
    false
  }

  override def valueOf(s: String): BaseActivation[String] = FalseActivation(s)
}