package it.gov.pagopa.payhub.auth.exception.custom;

public class InvalidAccessTokenException extends BaseBusinessException {
    public InvalidAccessTokenException(String code, String message) {
        super(code, message);
    }
}
