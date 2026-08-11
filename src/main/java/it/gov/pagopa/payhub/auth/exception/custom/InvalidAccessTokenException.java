package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;

public class InvalidAccessTokenException extends BaseBusinessException {
    public InvalidAccessTokenException(String code, String message) {
        super(code, message);
    }
}
