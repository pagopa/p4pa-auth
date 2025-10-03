package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AuditUtils {
  private static final String VENDOR = "PagoPa";
  private static final String PRODUCT = "P4PA-AUTH";
  private static final String VERSION = "1.0";
  private static final String DEVICE_VERSION = "1.0";

  public static String format(AuditLogDTO event) {
    String header = String.format("CEF:0|%s|%s|%s|%s|%s|",
        VENDOR,
        PRODUCT,
        DEVICE_VERSION,
        event.getAuditEventType().name(), // signatureId
        event.getDescription().replace("|", "_")
    );

    StringBuilder extensions = new StringBuilder();

    extensions.append("rt=").append(Instant.now().toString()).append(" "); // End Time/Real Time
    extensions.append("suser=").append(event.getMappedExternalUserId()).append(" "); // Source User
    //test null?
    extensions.append("msg=").append(event.getDescription()).append(" "); // Message

    // Map label2value
    if(event.getLabel2value()!=null){
      String customExtensions = event.getLabel2value().entrySet().stream()
          .map(e -> e.getKey() + "=" + e.getValue().replace(" ", "_"))
          .collect(Collectors.joining(" "));
      extensions.append(customExtensions);
    }
    log.info("Cef Message, Content: {}", header + extensions.toString().trim());
    return header + extensions.toString().trim();
  }
}
