package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import lombok.Getter;

@Getter
public class InvalidTokenException extends BaseBusinessException {

    public InvalidTokenException(String code, String message) {
        super(code, message);
    }
    public InvalidTokenException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
