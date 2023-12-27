package org.novi.activations.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.novi.core.exceptions.{ConfigurationParseException, ContextParseException}

class DateTimeActivationTest {

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def evaluateForDateInBetween(): Unit = {
    val dta: DateTimeActivation = new DateTimeActivation
    val result: Boolean = dta.valueOf(
      """
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).apply(
      """
                {
                    "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "15-12-2023 12:00"
                }
                """)
    assertThat(result).isTrue
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def evaluateForDateEqStartDate(): Unit = {
    val dta: DateTimeActivation = new DateTimeActivation
    val result: Boolean = dta.valueOf(
      """
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).apply(
      """
                {
                    "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "11-12-2023 12:00"
                }
                """)
    assertThat(result).isTrue
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def evaluateForDateEqEndDate(): Unit = {
    val dta: DateTimeActivation = new DateTimeActivation
    val result: Boolean = dta.valueOf(
      """
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).apply(
      """
                {
                    "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "20-12-2023 12:00"
                }
                """)
    assertThat(result).isFalse
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def evaluateForDateGtEndDate(): Unit = {
    val dta: DateTimeActivation = new DateTimeActivation
    val result: Boolean = dta.valueOf(
      """
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).apply(
      """
                {
                    "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "25-12-2023 12:00"
                }
                """)
    assertThat(result).isFalse
  }

  @Test
  @throws[ConfigurationParseException]
  @throws[ContextParseException]
  private[activations] def evaluateForDateLtStartDate(): Unit = {
    val dta: DateTimeActivation = new DateTimeActivation
    val result: Boolean = dta.valueOf(
      """
                {
                    "startDateTime":"11-12-2023 12:00",
                    "endDateTime":"20-12-2023 12:00"
                }
                """).apply(
      """
                {
                    "org.novi.activations.dsl.DateTimeActivation.currentDateTime": "05-12-2023 12:00"
                }
                """)
    assertThat(result).isFalse
  }
}
