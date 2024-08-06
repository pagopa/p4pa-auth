package it.gov.pagopa.payhub.auth.exception.custom;

public class OperatorNotFoundException extends RuntimeException {
    public OperatorNotFoundException(String message) {
        super(message);
    }
}
