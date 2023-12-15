package org.novi.web;

import org.junit.jupiter.api.Test;
import org.novi.core.activations.BaseActivation;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NoviConfigurationTest {

    @Test
    void foundActivationsTest() {
        NoviConfiguration noviConfig = new NoviConfiguration("./plugins");
        Map<String, BaseActivation> map = noviConfig.foundActivations();
        assertThat(map).containsKey("org.novi.activations.DateTimeActivation");
    }
}