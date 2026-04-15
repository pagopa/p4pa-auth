package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import lombok.Getter;

@Getter
public class TokenExpiredException extends BaseBusinessException {

    public TokenExpiredException(String message) {
        super(ErrorCodeConstants.ERROR_CODE_TOKEN_EXPIRED, message);
    }
}
