package it.gov.pagopa.payhub.auth.utils;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuditUtilsTest {

  private String VENDOR = "PagoPa";
  private String PRODUCT = "P4PA-AUTH";
  private String VERSION = "1.0";

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

    String expectedHeaderPrefix = String.format("CEF:0|%s|%s|%s|%s|%s|",
        VENDOR, PRODUCT, VERSION, AuditEventType.LOGIN_SUCCESS.name(), description);

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

  @Test
  void givenPipeWhenFormatThenVerifyEscapePipeInDescription() {
    // Given
    String description = "Error | Timeout";
    AuditLogDTO event = new AuditLogDTO(AuditEventType.LOGIN_FAILURE, "user", Collections.emptyMap(), description);

    // When
    String cefMessage = AuditUtils.format(event);

    // Then
    assertTrue(cefMessage.contains("LOGIN_FAILURE|Error _ Timeout"));
    assertTrue(cefMessage.contains("msg=Error | Timeout"));
  }

}