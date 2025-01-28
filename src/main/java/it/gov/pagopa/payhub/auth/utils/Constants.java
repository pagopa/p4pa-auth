package it.gov.pagopa.payhub.auth.utils;

import java.time.ZoneId;

public class Constants {
    private Constants(){}

    public static final ZoneId ZONEID = ZoneId.of("Europe/Rome");

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_OPER = "ROLE_OPER";
}
