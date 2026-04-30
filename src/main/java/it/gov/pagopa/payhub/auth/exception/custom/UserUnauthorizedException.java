package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class UserUnauthorizedException extends BaseBusinessException {

    public UserUnauthorizedException(String message){
        super(ErrorCodeConstants.ERROR_CODE_USER_UNAUTHORIZED, message);
    }
}
