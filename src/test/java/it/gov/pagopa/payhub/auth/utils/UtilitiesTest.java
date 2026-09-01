package it.gov.pagopa.payhub.auth.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UtilitiesTest {

  public static void setTraceId(String traceId) {
    setTraceId(traceId, null);
  }
  public static void setTraceId(String traceId, String spanId) {
    MDC.put("traceId", traceId);
    MDC.put("spanId", spanId);
  }
  public static void clearTraceIdContext(){
    MDC.clear();
  }

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

  @Test
  void testGetSpanId(){
    // Given
    String expectedResult = "SPANID";
    setTraceId("TRACEID", expectedResult);

    // When
    String result = Utilities.getSpanId();

    // Then
    Assertions.assertSame(expectedResult, result);
    clearTraceIdContext();
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
