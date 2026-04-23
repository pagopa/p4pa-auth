package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import lombok.Getter;

@Getter
public class InvalidTokenIssuerException extends BaseBusinessException {

    public InvalidTokenIssuerException(String message) {
        super(ErrorCodeConstants.ERROR_CODE_INVALID_SUBJECT_ISSUER, message);
    }
}
