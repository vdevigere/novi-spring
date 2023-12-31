package org.novi.activations.dsl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.random.JDKRandomGenerator
import org.apache.commons.math3.util.Pair
import org.novi.core.activations.{BaseActivation, BaseActivationFactory}
import org.novi.core.dsl.DslActivation

import java.util.{Collections, List, Map}
import java.{lang, util}

class WeightedRandomActivation(configuration: util.List[Pair[String, lang.Double]]) extends DslActivation(configuration) {
  def this() = {
    this(null)
  }

  override def evaluate(context: String): lang.Boolean = try {
    val mapper: ObjectMapper = new ObjectMapper
    val root: JsonNode = mapper.readTree(context)
    val parsedContext: util.Map[String, AnyRef] = mapper.treeToValue(root, classOf[util.Map[String, AnyRef]])
    val contextMap = parsedContext.get(getName).asInstanceOf[util.Map[String, AnyRef]]
    val seed = contextMap.get("seed").asInstanceOf[Int]
    val variantToCheck = contextMap.get("variantToCheck").asInstanceOf[String]
    val rnd = new JDKRandomGenerator(seed)
    val ed = new EnumeratedDistribution[String](rnd, this.configuration)
    ed.sample.equalsIgnoreCase(variantToCheck)
  } catch {
    case e: JsonProcessingException =>
      throw new RuntimeException(e)
  }


  override def apply(configuration: String): BaseActivation[util.List[Pair[String, lang.Double]]] = WeightedRandomActivation.apply(configuration)
}

object WeightedRandomActivation extends BaseActivationFactory[util.List[Pair[String, lang.Double]]]{
  override def apply(configuration: String): WeightedRandomActivation = {
    val variant_and_weights: util.List[Pair[String, lang.Double]] = if (configuration != null) {
      val tref: TypeReference[util.Map[String, lang.Double]] = new TypeReference[util.Map[String, lang.Double]]() {}
      val mapper = new ObjectMapper
      val parsedConfig: util.Map[String, lang.Double] = mapper.readValue(configuration, tref)
      parsedConfig.entrySet.stream.map((e: util.Map.Entry[String, lang.Double]) => Pair.create(e.getKey, e.getValue)).toList
    } else {
      Collections.emptyList()
    }
    new WeightedRandomActivation(variant_and_weights)
  }

}