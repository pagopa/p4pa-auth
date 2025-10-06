package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.utils.AuditUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "KAFKA_LOGGER")
public class AuditLoggerServiceImpl implements AuditLoggerService {
  @Override
  public void log(AuditEventType auditEventType, Map<String, String> label2value, String description) {
    AuditLogDTO event = new AuditLogDTO(auditEventType, MDC.get("externalUserId"), label2value, description);
    String cefMessage = AuditUtils.format(event);
    log.info(cefMessage);
  }
}
