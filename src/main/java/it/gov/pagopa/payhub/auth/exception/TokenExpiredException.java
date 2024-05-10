package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.constants.AuthConstants;
import it.gov.pagopa.payhub.common.web.exception.ServiceException;

public class TokenExpiredException extends ServiceException {
    public TokenExpiredException(String message) {
        this(AuthConstants.ExceptionCode.TOKEN_DATE_EXPIRED, message);
    }

    public TokenExpiredException(String code, String message) {
        this(code, message, false, null);
    }

    public TokenExpiredException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(code, message,printStackTrace, ex);
    }
}
