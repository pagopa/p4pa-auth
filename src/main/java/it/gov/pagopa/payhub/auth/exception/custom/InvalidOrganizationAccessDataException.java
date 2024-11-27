package it.gov.pagopa.payhub.auth.exception.custom;

public class InvalidOrganizationAccessDataException extends RuntimeException {
    public InvalidOrganizationAccessDataException(String message) {
        super(message);
    }
}
