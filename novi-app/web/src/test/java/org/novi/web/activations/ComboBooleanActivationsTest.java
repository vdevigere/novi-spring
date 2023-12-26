package org.novi.web.activations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.FoundActivations;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComboBooleanActivationsTest {

    public static final String A_CONFIG = """
            !org.novi.dsl.activations.FalseActivationConfig("False-1") & (org.novi.dsl.activations.FalseActivationConfig("False-2") | org.novi.dsl.activations.TrueActivationConfig("True-3"))
            """;
    public static final String B_CONFIG = """
            org.novi.dsl.activations.FalseActivationConfig("False-1") & (org.novi.dsl.activations.FalseActivationConfig("False-2") | org.novi.dsl.activations.TrueActivationConfig("True-3"))
            """;

    private List<ActivationConfig> mockActivationConfigs() {
        ActivationConfig a = mock(ActivationConfig.class);
        when(a.getName()).thenReturn("A");
        when(a.getConfig()).thenReturn(A_CONFIG);
        ActivationConfig b = mock(ActivationConfig.class);
        when(b.getName()).thenReturn("B");
        when(b.getConfig()).thenReturn(B_CONFIG);
        return Arrays.asList(a, b);
    }


    @BeforeEach
    private void mockRegistry() throws ConfigurationParseException {
        BaseActivation<String> alwaysTrue = mock(BaseActivation.class);
        when(alwaysTrue.configuration(A_CONFIG)).thenReturn(alwaysTrue);
        when(alwaysTrue.apply("{}")).thenReturn(true);
        BaseActivation<String> alwaysFalse = mock(BaseActivation.class);
        when(alwaysFalse.configuration(B_CONFIG)).thenReturn(alwaysFalse);
        when(alwaysFalse.apply("{}")).thenReturn(false);

        FoundActivations.REGISTRY.getMap().put("A", alwaysTrue);
        FoundActivations.REGISTRY.getMap().put("B", alwaysFalse);
    }

    @Test
    public void testWhenConfiguredWithIterable() {
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ComboBooleanActivations cmb = new ComboBooleanActivations(null);
        Boolean andResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.AND).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.OR).apply("{}");
        assertThat(orResult).isTrue();
    }


    @Test
    public void testWhenConfiguredWithConfigMap() {
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(mockRepository.findAllById(ids)).thenReturn(activationConfigs);
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
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(mockRepository.findAllById(ids)).thenReturn(activationConfigs);
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
