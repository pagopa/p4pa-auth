package it.gov.pagopa.payhub.auth.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UtilitiesTest {

  @Test
  void testGetTraceId(){
    // Given
    String expectedResult = "TRACEID";
    setTraceId(expectedResult);

    // When
    String result = Utilities.getTraceId();

    // Then
    Assertions.assertSame(expectedResult, result);
    clearTraceIdContext();
  }

  public static void setTraceId(String traceId) {
    MDC.put("traceId", traceId);
  }
  public static void clearTraceIdContext(){
    MDC.clear();
  }

  @Test
  void testLocalDatetimeToOffsetDateTimeWithNull() {
    assertNull(Utilities.localDatetimeToOffsetDateTime(null), "The result should be null for a null input.");
  }

  @Test
  void testLocalDatetimeToOffsetDateTime() {
    OffsetDateTime expectedOffsetDateTime = OffsetDateTime.now();

    OffsetDateTime result = Utilities.localDatetimeToOffsetDateTime(expectedOffsetDateTime.toLocalDateTime());

    assertEquals(expectedOffsetDateTime, result);
  }
}
