package it.gov.pagopa.payhub.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.utils.AuditUtils;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLoggerServiceImplTest {
  private AuditLoggerServiceImpl auditLoggerService;

  private final AuditEventType eventType = AuditEventType.LOGIN_SUCCESS;
  private final Map<String, String> labels = Map.of("ORGID", "1");
  private final String description = "Test Login";
  private final String expectedCefMessage = "CEF:TEST_MESSAGE";


  @BeforeEach
  void setUp() {
    auditLoggerService = new AuditLoggerServiceImpl();
  }

  @Test
  void whenLogThenLogCefMessage() {
    try (MockedStatic<AuditUtils> mockedAuditUtils = mockStatic(AuditUtils.class)) {
      mockedAuditUtils.when(() -> AuditUtils.format(any(AuditLogDTO.class)))
          .thenReturn(expectedCefMessage);
      // When
      auditLoggerService.log(eventType, labels, description);
      // Then
      mockedAuditUtils.verify(() -> AuditUtils.format(any(AuditLogDTO.class)), times(1));
    }
  }

  @Test
  void givenNullMapWhenLogThenNoError() {
    try (MockedStatic<AuditUtils> mockedAuditUtils = mockStatic(AuditUtils.class)) {
      mockedAuditUtils.when(() -> AuditUtils.format(any(AuditLogDTO.class)))
          .thenReturn(expectedCefMessage);
      // When
      auditLoggerService.log(eventType, null, description);
      // Then
      mockedAuditUtils.verify(() -> AuditUtils.format(any(AuditLogDTO.class)), times(1));
    }
  }
}