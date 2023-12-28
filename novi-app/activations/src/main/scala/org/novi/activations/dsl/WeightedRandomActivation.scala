package org.novi.activations.dsl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.random.JDKRandomGenerator
import org.apache.commons.math3.util.Pair
import org.novi.core.activations.BaseActivation
import org.novi.core.dsl.DslActivation

import java.util.{List, Map}
import java.{lang, util}

class WeightedRandomActivation(configuration: String) extends DslActivation(configuration) {

  val tref: TypeReference[util.Map[String, lang.Double]] = new TypeReference[util.Map[String, lang.Double]]() {}
  val mapper = new ObjectMapper
  val parsedConfig: util.Map[String, lang.Double] = mapper.readValue(configuration, tref)
  private var variant_and_weights: util.List[Pair[String, lang.Double]] = parsedConfig.entrySet.stream.map((e: util.Map.Entry[String, lang.Double]) => Pair.create(e.getKey, e.getValue)).toList


  def this() = {
    this(null)
  }

  override def apply(context: String): lang.Boolean = try {
    val mapper: ObjectMapper = new ObjectMapper
    val root: JsonNode = mapper.readTree(context)
    val parsedContext: util.Map[String, AnyRef] = mapper.treeToValue(root, classOf[util.Map[String, AnyRef]])
    val contextMap = parsedContext.get(getName).asInstanceOf[util.Map[String, AnyRef]]
    val seed = contextMap.get("seed").asInstanceOf[Int]
    val variantToCheck = contextMap.get("variantToCheck").asInstanceOf[String]
    val rnd = new JDKRandomGenerator(seed)
    val ed = new EnumeratedDistribution[String](rnd, this.variant_and_weights)
    ed.sample.equalsIgnoreCase(variantToCheck)
  } catch {
    case e: JsonProcessingException =>
      throw new RuntimeException(e)
  }


  override def configuration(configuration: String): BaseActivation[String] = WeightedRandomActivation(configuration)
}
