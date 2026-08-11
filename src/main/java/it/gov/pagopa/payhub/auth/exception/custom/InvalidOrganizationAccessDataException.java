package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;

public class InvalidOrganizationAccessDataException extends BaseBusinessException {
    public InvalidOrganizationAccessDataException(String code, String message) {
        super(code, message);
    }
}
