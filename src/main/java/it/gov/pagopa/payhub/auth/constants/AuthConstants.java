package it.gov.pagopa.payhub.auth.constants;

public class AuthConstants {
    private AuthConstants() {}
    public static final class ExceptionCode {

        public static final String TOKEN_DATE_EXPIRED = "AUTH_TOKEN_EXPIRED_DATE";
        public static final String GENERIC_ERROR = "AUTH_GENERIC_ERROR";
        public static final String INVALID_REQUEST = "AUTH_INVALID_REQUEST";
        public static final String INVALID_TOKEN = "AUTH_INVALID_TOKEN";

        private ExceptionCode() {}
    }
}
