package org.novi.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Pair;
import org.novi.core.activations.BaseActivation;
import org.novi.core.exceptions.ConfigurationParseException;

import java.util.List;
import java.util.Map;

public class WeightedRandomActivation implements BaseActivation<List<Pair<String, Double>>> {

    private List<Pair<String, Double>> configuration;

    @Override
    public WeightedRandomActivation configuration(String configuration) throws ConfigurationParseException {
        try {
            TypeReference<Map<String, Double>> tref = new TypeReference<>() {
            };
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Double> parsedConfig = mapper.readValue(configuration, tref);
            this.configuration = parsedConfig.entrySet().stream().map(e -> Pair.create(e.getKey(), e.getValue())).toList();
            return this;
        } catch (JsonProcessingException e) {
            throw new ConfigurationParseException(e);
        }
    }

    @Override
    public Boolean apply(String context){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(context);
            Map<String, Object> contextMap = mapper.treeToValue(root, Map.class);
            return evaluateFor(contextMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean evaluateFor(Map<String, Object> context) {
        Map<String, Object> contextMap = (Map<String, Object>) context.get(getName());
        int seed = (int) contextMap.get("seed");
        String variantToCheck = (String) contextMap.get("variantToCheck");
        RandomGenerator rnd = new JDKRandomGenerator(seed);
        EnumeratedDistribution<String> ed = new EnumeratedDistribution<>(rnd, this.configuration());
        return ed.sample().equalsIgnoreCase(variantToCheck);
    }

    @Override
    public List<Pair<String, Double>> configuration() {
        return configuration;
    }
}
