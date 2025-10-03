package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import java.util.Map;

public interface AuditLoggerService {
  void log(AuditEventType auditEventType, String mappedExternalUserId, Map<String, String> label2value, String description);
}
