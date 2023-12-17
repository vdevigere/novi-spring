package org.novi.activations;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;
import org.novi.core.activations.BaseConfiguredActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.core.exceptions.ContextParseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WeightedRandomActivationTest {

    @Test
    void parseConfiguration() throws ConfigurationParseException {
        WeightedRandomActivation wra = new WeightedRandomActivation();
        String config = """
                {
                "SampleA":50.0,
                "SampleB":25.0,
                "SampleC":25.0
                }
                """;
        BaseConfiguredActivation<List<Pair<String, Double>>> retVal = wra.whenConfiguredWith(config);
        assertThat(retVal.getConfiguration()).contains(Pair.create("SampleA", 50.0), Pair.create("SampleB", 25.0), Pair.create("SampleC", 25.0));
    }

    @Test
    void testEvaluateToTrue() throws ConfigurationParseException, ContextParseException {
        WeightedRandomActivation wra = new WeightedRandomActivation();
        String config = """
                {
                "SampleA":100.0,
                "SampleB":0,
                "SampleC":0
                }
                """;
        String context = """
                {
                    "org.novi.activations.WeightedRandomActivation":{
                        "seed": 200,
                        "variantToCheck": "SampleA"
                    }
                }
                """;
        assertThat(wra.whenConfiguredWith(config).evaluateFor(context)).isTrue();
    }

    @Test
    void testEvaluateToFalse() throws ConfigurationParseException, ContextParseException {
        WeightedRandomActivation wra = new WeightedRandomActivation();
        String config = """
                {
                "SampleA":0.0,
                "SampleB":100,
                "SampleC":0
                }
                """;
        String context = """
                {
                    "org.novi.activations.WeightedRandomActivation":{
                        "seed": 200,
                        "variantToCheck": "SampleA"
                    }
                }
                """;
        assertThat(wra.whenConfiguredWith(config).evaluateFor(context)).isFalse();
    }
}