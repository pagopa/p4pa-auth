package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.utils.AuditUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "KAFKA_LOGGER")
public class AuditLoggerServiceImpl implements AuditLoggerService {
  @Override
  public void log(AuditEventType auditEventType, String mappedExternalUserId,
      Map<String, String> label2value, String description) {
    AuditLogDTO event = new AuditLogDTO(auditEventType, mappedExternalUserId, label2value, description);
    String cefMessage = AuditUtils.format(event);
    log.info("TEST STATICO STRINGA");
  }
}
