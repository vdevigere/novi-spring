package org.novi.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.BaseConfiguredActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

public class WeightedRandomActivation implements BaseActivation {

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public BaseConfiguredActivation<List<Pair<String, Double>>> whenConfiguredWith(String configuration) throws ConfigurationParseException {
        try {
            TypeReference<Map<String, Double>> tref = new TypeReference<>() {
            };
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Double> parsedConfig = mapper.readValue(configuration, tref);
            List<Pair<String, Double>> sampleSet = parsedConfig.entrySet().stream().map(e -> Pair.create(e.getKey(), e.getValue())).toList();
            return new BaseConfiguredActivation<>(sampleSet) {

                @Override
                public boolean evaluateFor(Map<String, Object> context) {
                    Map<String, Object> contextMap = (Map<String, Object>) context.get(WeightedRandomActivation.this.getName());
                    int seed = (int) contextMap.get("seed");
                    String variantToCheck = (String) contextMap.get("variantToCheck");
                    RandomGenerator rnd = new JDKRandomGenerator(seed);
                    EnumeratedDistribution<String> ed = new EnumeratedDistribution<>(rnd, this.getConfiguration());
                    return ed.sample().equalsIgnoreCase(variantToCheck);
                }
            };

        } catch (JsonProcessingException e) {
            throw new ConfigurationParseException(e);
        }
    }
}
