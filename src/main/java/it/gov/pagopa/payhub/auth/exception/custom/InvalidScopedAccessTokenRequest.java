package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import lombok.Getter;

@Getter
public class InvalidScopedAccessTokenRequest extends BaseBusinessException {

    public InvalidScopedAccessTokenRequest(String message) {
        super(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, message);
    }
}
