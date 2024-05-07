package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.constants.AuthConstants;
import it.gov.pagopa.payhub.common.web.exception.ServiceException;

public class InvalidTokenException extends ServiceException {
    public InvalidTokenException(String message) {
        this(AuthConstants.ExceptionCode.INVALID_TOKEN, message);
    }

    public InvalidTokenException(String code, String message) {
        this(code, message, false, null);
    }

    public InvalidTokenException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(code, message,printStackTrace, ex);
    }
}
