package org.novi.web;

import org.junit.jupiter.api.Test;
import org.novi.core.activations.FoundActivations;

import static org.assertj.core.api.Assertions.assertThat;

class NoviConfigurationTest {

    @Test
    void foundActivationsTest() {
        NoviConfiguration noviConfig = new NoviConfiguration("./plugins", null);
        assertThat(FoundActivations.REGISTRY.getMap()).containsKey("org.novi.web.activations.ComboBooleanActivations");
    }
}