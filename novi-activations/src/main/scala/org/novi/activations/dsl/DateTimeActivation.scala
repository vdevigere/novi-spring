package org.novi.activations.dsl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.novi.activations.dsl.DateTimeActivation.{DATE_FORMAT, DateRangeRecord}
import org.novi.core.activations.{BaseActivation, BaseActivationFactory}
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Date, Map}
import java.{lang, util}

class DateTimeActivation(configuration: DateRangeRecord) extends DslActivation(configuration) {
  private val logger = LoggerFactory.getLogger(classOf[DateTimeActivation])

  def this() = {
    this(null)
  }

  override def evaluate(context: String): lang.Boolean = {
    try {
      val mapper = new ObjectMapper
      val root = mapper.readTree(context)
      val contextMap = mapper.treeToValue(root, classOf[util.Map[String, AnyRef]])
      val df = new SimpleDateFormat(DATE_FORMAT)
      try {
        val currentDateTime = df.parse(contextMap.get(getName + ".currentDateTime").asInstanceOf[String])
        logger.debug("Checking for {} <= {} < {}", configuration.startDateTime, currentDateTime, configuration.endDateTime)
        configuration.startDateTime.compareTo(currentDateTime) <= 0 && configuration.endDateTime.compareTo(currentDateTime) > 0
      } catch {
        case e: ParseException =>
          throw new RuntimeException(e)
      }
    } catch {
      case e: JsonProcessingException =>
        throw new RuntimeException(e)
    }
  }

  override def apply(configuration: String): BaseActivation[DateRangeRecord] = DateTimeActivation.apply(configuration)
}

object DateTimeActivation extends BaseActivationFactory[DateRangeRecord] {
  case class DateRangeRecord(startDateTime: util.Date, endDateTime: util.Date)

  private val DATE_FORMAT = "dd-MM-yyyy hh:mm"


  override def apply(configuration: String): DateTimeActivation = {
    val mapper = new ObjectMapper
    mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT))
    mapper.registerModule(DefaultScalaModule)
    val dateRange: DateRangeRecord = if (configuration != null) mapper.readValue(configuration, new TypeReference[DateRangeRecord]() {}) else DateRangeRecord(util.Date(), util.Date())
    new DateTimeActivation(dateRange)
  }

}
