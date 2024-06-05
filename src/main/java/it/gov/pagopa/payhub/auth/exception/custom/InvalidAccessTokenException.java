package it.gov.pagopa.payhub.auth.exception.custom;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String message) {
        super(message);
    }
}
