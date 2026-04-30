package it.gov.pagopa.payhub.auth.exception.custom;

public class InvalidOrganizationAccessDataException extends BaseBusinessException {
    public InvalidOrganizationAccessDataException(String code, String message) {
        super(code, message);
    }
}
