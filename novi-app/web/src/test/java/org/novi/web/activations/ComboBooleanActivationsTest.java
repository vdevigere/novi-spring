package org.novi.web.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.FoundActivations;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComboBooleanActivationsTest {
    BaseActivation<String> alwaysTrue = new BaseActivation<>() {
        private String config;

        @Override
        public BaseActivation<String> configuration(String configuration) throws ConfigurationParseException {
            this.config = configuration;
            return this;
        }

        @Override
        public Boolean apply(String context) {
            return true;
        }



        @Override
        public String configuration() {
            return config;
        }
    };

    BaseActivation<String> alwaysFalse = new BaseActivation<String>() {
        private String config;

        @Override
        public BaseActivation<String> configuration(String configuration) throws ConfigurationParseException {
            this.config = configuration;
            return this;
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
            return false;
        }

        @Override
        public String configuration() {
            return config;
        }
    };


    @BeforeEach
    public void initFoundActivations(){
        FoundActivations.REGISTRY.getMap().put("A", alwaysTrue);
        FoundActivations.REGISTRY.getMap().put("B", alwaysFalse);
    }

    @Test
    public void testWhenConfiguredWithIterable() {
        ActivationConfig a = mock(ActivationConfig.class);
        when(a.getName()).thenReturn("A");
        ActivationConfig b = mock(ActivationConfig.class);
        when(b.getName()).thenReturn("B");
        List<ActivationConfig> activationConfigs = Arrays.asList(a, b);
        ActivationConfigRepository mockRepository = null;
        ComboBooleanActivations cmb = new ComboBooleanActivations(mockRepository);
        Boolean andResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.AND).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.OR).apply("{}");
        assertThat(orResult).isTrue();
    }

    @Test
    public void testWhenConfiguredWithConfigMap() {
        ActivationConfig a = mock(ActivationConfig.class);
        when(a.getName()).thenReturn("A");
        ActivationConfig b = mock(ActivationConfig.class);
        when(b.getName()).thenReturn("B");
        List<ActivationConfig> activationConfigs = Arrays.asList(a, b);
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        when(mockRepository.findAllById(Mockito.any())).thenReturn(activationConfigs);
        ComboBooleanActivations cmb = new ComboBooleanActivations(mockRepository);
        ComboBooleanActivations.ConfigRecord configMapAND = new ComboBooleanActivations.ConfigRecord(Arrays.asList(1L, 2L), ComboBooleanActivations.OPERATION.AND);
        ComboBooleanActivations.ConfigRecord configMapOR = new ComboBooleanActivations.ConfigRecord(Arrays.asList(1L, 2L), ComboBooleanActivations.OPERATION.OR);

        Boolean andResult = cmb.whenConfiguredWith(configMapAND).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(configMapOR).apply("{}");
        assertThat(orResult).isTrue();
    }

    @Test
    public void testWhenConfiguredWithString() throws ConfigurationParseException {
        ActivationConfig a = mock(ActivationConfig.class);
        when(a.getName()).thenReturn("A");
        ActivationConfig b = mock(ActivationConfig.class);
        when(b.getName()).thenReturn("B");
        List<ActivationConfig> activationConfigs = Arrays.asList(a, b);
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        when(mockRepository.findAllById(Mockito.any())).thenReturn(activationConfigs);
        ComboBooleanActivations cmb = new ComboBooleanActivations(mockRepository);

        Boolean andResult = cmb.configuration("""
                {
                    "activationIds":[1,2],
                    "operation":"AND"
                }
                """).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.configuration("""
                {
                    "activationIds":[1,2],
                    "operation":"OR"
                }
                """).apply("{}");
        assertThat(orResult).isTrue();
    }
}
