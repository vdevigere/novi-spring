package org.novi.core.dsl

import org.novi.core.activations.BaseActivation
import org.slf4j.LoggerFactory


class AndActivation(op1: DslActivation[?], op2: DslActivation[?], configuration: String = null) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[AndActivation])

  override def evaluate(context: String): java.lang.Boolean = {
    val result = op1.evaluate(context) & op2.evaluate(context)
    logger.debug("{} & {} = {}", op1.configuration, op2.configuration, result)
    result
  }

  override def apply(configuration: String): BaseActivation[String] = throw UnsupportedOperationException()
}
