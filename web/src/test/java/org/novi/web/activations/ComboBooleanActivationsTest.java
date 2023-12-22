package org.novi.web.activations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.BaseConfiguredActivation;
import org.novi.core.activations.FoundActivations;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComboBooleanActivationsTest {
    BaseActivation alwaysTrue = configuration -> new BaseConfiguredActivation<String>(configuration) {
        @Override
        public Boolean evaluateFor(Map<String, Object> context) {
            return true;
        }
    };

    BaseActivation alwaysFalse = configuration -> new BaseConfiguredActivation<String>(configuration) {
        @Override
        public Boolean evaluateFor(Map<String, Object> context) {
            return false;
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
        Boolean andResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.AND).evaluateFor(Collections.EMPTY_MAP);
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.OR).evaluateFor(Collections.EMPTY_MAP);
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

        Boolean andResult = cmb.whenConfiguredWith(configMapAND).evaluateFor(Collections.EMPTY_MAP);
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(configMapOR).evaluateFor(Collections.EMPTY_MAP);
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

        Boolean andResult = cmb.whenConfiguredWith("""
                {
                    "activationIds":[1,2],
                    "operation":"AND"
                }
                """).evaluateFor(Collections.EMPTY_MAP);
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith("""
                {
                    "activationIds":[1,2],
                    "operation":"OR"
                }
                """).evaluateFor(Collections.EMPTY_MAP);
        assertThat(orResult).isTrue();
    }
}
