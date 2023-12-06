package org.novi.activations;

import org.novi.core.exceptions.ConfigurationParseException;
import org.novi.core.exceptions.ContextParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeActivationTest {

    @Test
    void evaluateForDateInBetween() throws ConfigurationParseException, ContextParseException {
        DateTimeActivation dta = new DateTimeActivation();
        boolean result = dta.whenConfiguredWith("""
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).evaluateFor("""
                {
                    "org.novi.activations.DateTimeActivation.currentDateTime": "15-12-2023 12:00"
                }
                """);
        assertThat(result).isTrue();
    }

    @Test
    void evaluateForDateEqStartDate() throws ConfigurationParseException, ContextParseException {
        DateTimeActivation dta = new DateTimeActivation();
        boolean result = dta.whenConfiguredWith("""
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).evaluateFor("""
                {
                    "org.novi.activations.DateTimeActivation.currentDateTime": "11-12-2023 12:00"
                }
                """);
        assertThat(result).isTrue();
    }

    @Test
    void evaluateForDateEqEndDate() throws ConfigurationParseException, ContextParseException {
        DateTimeActivation dta = new DateTimeActivation();
        boolean result = dta.whenConfiguredWith("""
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).evaluateFor("""
                {
                    "org.novi.activations.DateTimeActivation.currentDateTime": "20-12-2023 12:00"
                }
                """);
        assertThat(result).isFalse();
    }

    @Test
    void evaluateForDateGtEndDate() throws ConfigurationParseException, ContextParseException {
        DateTimeActivation dta = new DateTimeActivation();
        boolean result = dta.whenConfiguredWith("""
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).evaluateFor("""
                {
                    "org.novi.activations.DateTimeActivation.currentDateTime": "25-12-2023 12:00"
                }
                """);
        assertThat(result).isFalse();
    }

    @Test
    void evaluateForDateLtStartDate() throws ConfigurationParseException, ContextParseException {
        DateTimeActivation dta = new DateTimeActivation();
        boolean result = dta.whenConfiguredWith("""
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).evaluateFor("""
                {
                    "org.novi.activations.DateTimeActivation.currentDateTime": "05-12-2023 12:00"
                }
                """);
        assertThat(result).isFalse();
    }
}