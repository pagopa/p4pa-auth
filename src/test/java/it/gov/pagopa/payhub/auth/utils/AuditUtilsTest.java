package it.gov.pagopa.payhub.auth.utils;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AuditUtilsTest {

  @Test
  void givenEventWhenFormatThenCefMessage() {
    // Given
    String userId = "userId";
    String description = "Login success";
    Map<String, String> labels = Map.of(
        "clientId", "clientId",
        "scope", "read"
    );

    AuditLogDTO event = new AuditLogDTO(AuditEventType.LOGIN_SUCCESS, userId, labels, description);

    String expectedHeaderPrefix = String.format("CEF:0|%s|%s|%s|%s|%s|%s|",
        "PiattaformaUnitaria", "P4PA-AUTH", "1.0", AuditEventType.LOGIN_SUCCESS.name(), description, 0);

    // When
    String cefMessage = AuditUtils.format(event);

    // Then
    assertNotNull(cefMessage);
    assertTrue(cefMessage.startsWith(expectedHeaderPrefix));
    assertTrue(cefMessage.contains("rt="));
    assertTrue(cefMessage.contains("suser=" + userId));
    assertTrue(cefMessage.contains("msg=" + description));
    assertTrue(cefMessage.contains("clientId=clientId"));
    assertTrue(cefMessage.contains("scope=read"));
  }

  @Test
  void givenNullMapWhenFormatThenVerify() {
    // Given
    String userId = "user";
    String description = "Logout";

    AuditLogDTO event = new AuditLogDTO(AuditEventType.LOGOUT, userId, null, description);

    // When
    String cefMessage = AuditUtils.format(event);

    // Then
    assertNotNull(cefMessage);
    assertTrue(cefMessage.contains("suser=user"));

    String expectedEnd = String.format("suser=%s msg=%s", userId, description);
    assertTrue(cefMessage.endsWith(expectedEnd));
    assertTrue(cefMessage.lastIndexOf(" ") < cefMessage.lastIndexOf(description));
  }

  @ParameterizedTest
  @CsvSource({
          "|, \\|, |",
          "\\, \\\\, \\\\",
          "=, =, \\="
  })
  void givenCharToEscapeWhenFormatThenVerifyEscapeCharInDescription(char charToEscape, String expectedCharInHeader, String expectedCharInExtension) {
    // Given
    String description = String.format("Error %c Timeout", charToEscape);
    AuditLogDTO event = new AuditLogDTO(AuditEventType.LOGIN_FAILURE, "user", Collections.emptyMap(), description);

    // When
    String cefMessage = AuditUtils.format(event);

    // Then
    assertTrue(cefMessage.contains(String.format("LOGIN_FAILURE|Error %s Timeout", expectedCharInHeader)));
    assertTrue(cefMessage.contains(String.format("msg=Error %s Timeout", expectedCharInExtension)));
  }

}