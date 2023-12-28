package org.novi.activations.dsl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.novi.core.activations.BaseActivation
import org.novi.core.dsl.DslActivation
import org.slf4j.LoggerFactory

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Date, Map}
import java.{lang, util}

class DateTimeActivation(configuration: String) extends DslActivation(configuration) {
  private val DATE_FORMAT = "dd-MM-yyyy hh:mm"

  private[activations] val logger = LoggerFactory.getLogger(classOf[DateTimeActivation])
  private case class DateRangeRecord(startDateTime: util.Date, endDateTime: util.Date)

  val mapper = new ObjectMapper
  mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT))
  private val dateRange: DateRangeRecord = mapper.readValue(configuration, new TypeReference[DateRangeRecord]() {})

  override def apply(context: String): lang.Boolean = {
    try {
      val mapper = new ObjectMapper
      val root = mapper.readTree(context)
      val contextMap = mapper.treeToValue(root, classOf[util.Map[String, AnyRef]])
      val df = new SimpleDateFormat(DATE_FORMAT)
      try {
        val currentDateTime = df.parse(contextMap.get(getName + ".currentDateTime").asInstanceOf[String])
        logger.debug("Checking for {} <= {} < {}", this.dateRange.startDateTime, currentDateTime, this.dateRange.endDateTime)
        this.dateRange.startDateTime.compareTo(currentDateTime) <= 0 && this.dateRange.endDateTime.compareTo(currentDateTime) > 0
      } catch {
        case e: ParseException =>
          throw new RuntimeException(e)
      }
    } catch {
      case e: JsonProcessingException =>
        throw new RuntimeException(e)
    }
  }

  override def valueOf(configuration: String): BaseActivation[String] = DateTimeActivation(configuration)
}
