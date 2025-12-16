package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.auth.dto.AuditLogDTO;
import java.time.Instant;
import java.util.stream.Collectors;

public final class AuditUtils {
  private AuditUtils() {}

  private static final String VENDOR = "PiattaformaUnitaria";
  private static final String PRODUCT = "P4PA-AUTH";
  private static final String VERSION = "1.0";

  public static String format(AuditLogDTO event) {
    String header = String.format("CEF:0|%s|%s|%s|%s|%s|%s|",
        VENDOR,
        PRODUCT,
        VERSION,
        event.getAuditEventType().name(), // signatureId
        escapeCefHeaderField(event.getDescription()),
        0 //Severity, default to zero
    );

    StringBuilder extensions = new StringBuilder();
    extensions.append("rt=").append(Instant.now().toString()).append(" "); // End Time/Real Time
    extensions.append("suser=").append(escapeCefExtensionField(event.getMappedExternalUserId())).append(" "); // Source User
    extensions.append("msg=").append(escapeCefExtensionField(event.getDescription())).append(" "); // Message
    extensions.append("traceId=").append(event.getTraceId()).append(" ");

    // Map label2value
    if(event.getLabel2value()!=null){
      String customExtensions = event.getLabel2value().entrySet().stream()
          .map(e -> e.getKey() + "=" + escapeCefExtensionField(e.getValue()))
          .collect(Collectors.joining(" "));
      extensions.append(customExtensions);
    }
    return header + extensions.toString().trim();
  }

  private static String escapeCefHeaderField(String cefHeaderField) {
    return cefHeaderField
            .replace("\\", "\\\\")
            .replace("|", "\\|");
  }

  private static String escapeCefExtensionField(String cefExtensionField) {
    return cefExtensionField
            .replace("\\", "\\\\")
            .replace("=", "\\=");
  }

}
