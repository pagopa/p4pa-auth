package it.gov.pagopa.payhub.auth.utils;

import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class Utilities {
    private Utilities() {}

    public static String getTraceId(){
        return MDC.get("traceId");
    }

    public static String getSpanId(){
        return MDC.get("spanId");
    }

    public static OffsetDateTime localDatetimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null
            ? ZonedDateTime.of(localDateTime, Constants.ZONEID).toOffsetDateTime()
            : null;
    }
}
