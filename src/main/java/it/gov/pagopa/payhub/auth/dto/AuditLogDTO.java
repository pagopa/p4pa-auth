package it.gov.pagopa.payhub.auth.dto;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AuditLogDTO {
  private AuditEventType auditEventType;
  private String mappedExternalUserId;
  private Map<String, String> label2value;
  private String description;
}
