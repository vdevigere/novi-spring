package org.novi.web.activations;

import org.junit.jupiter.api.Test;
import org.novi.core.ActivationConfig;
import org.novi.core.activations.BaseActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.persistence.ActivationConfigRepository;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComboBooleanActivationsTest {

    public static final String A_CONFIG = """
            !FalseActivationConfig("False-1") & (FalseActivationConfig("False-2") | TrueActivationConfig("True-3"))
            """;
    public static final String B_CONFIG = """
            FalseActivationConfig("False-1") & (FalseActivationConfig("False-2") | TrueActivationConfig("True-3"))
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

    private ScriptEngine mockScriptEngine() throws ScriptException {
        BaseActivation<String> alwaysTrue = mock(BaseActivation.class);
        when(alwaysTrue.apply("{}")).thenReturn(true);
        BaseActivation<String> alwaysFalse = mock(BaseActivation.class);
        when(alwaysFalse.apply("{}")).thenReturn(false);

        ScriptEngine mockEngine = mock(ScriptEngine.class);
        when(mockEngine.eval(A_CONFIG)).thenReturn(alwaysTrue);
        when(mockEngine.eval(B_CONFIG)).thenReturn(alwaysFalse);
        return mockEngine;
    }

    @Test
    public void testWhenConfiguredWithIterable() throws ScriptException {
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ComboBooleanActivations cmb = new ComboBooleanActivations(null, mockScriptEngine());
        Boolean andResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.AND).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(activationConfigs, ComboBooleanActivations.OPERATION.OR).apply("{}");
        assertThat(orResult).isTrue();
    }


    @Test
    public void testWhenConfiguredWithConfigMap() throws ScriptException {
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(mockRepository.findAllById(ids)).thenReturn(activationConfigs);
        ComboBooleanActivations cmb = new ComboBooleanActivations(mockRepository, mockScriptEngine());
        ComboBooleanActivations.ConfigRecord configMapAND = new ComboBooleanActivations.ConfigRecord(Arrays.asList(1L, 2L), ComboBooleanActivations.OPERATION.AND);
        ComboBooleanActivations.ConfigRecord configMapOR = new ComboBooleanActivations.ConfigRecord(Arrays.asList(1L, 2L), ComboBooleanActivations.OPERATION.OR);

        Boolean andResult = cmb.whenConfiguredWith(configMapAND).apply("{}");
        assertThat(andResult).isFalse();
        Boolean orResult = cmb.whenConfiguredWith(configMapOR).apply("{}");
        assertThat(orResult).isTrue();
    }

    @Test
    public void testWhenConfiguredWithString() throws ConfigurationParseException, ScriptException {
        List<ActivationConfig> activationConfigs = mockActivationConfigs();
        ActivationConfigRepository mockRepository = mock(ActivationConfigRepository.class);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(mockRepository.findAllById(ids)).thenReturn(activationConfigs);
        ComboBooleanActivations cmb = new ComboBooleanActivations(mockRepository, mockScriptEngine());

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
