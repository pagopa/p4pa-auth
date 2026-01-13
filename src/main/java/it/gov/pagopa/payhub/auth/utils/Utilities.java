package it.gov.pagopa.payhub.auth.utils;

import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class Utilities {
    private Utilities() {}

    public static String getTraceId(){
        return MDC.get("traceId");
    }

    public static OffsetDateTime localDatetimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null
            ? localDateTime.atOffset(ZoneId.systemDefault().getRules().getOffset(localDateTime))
            : null;
    }
}
