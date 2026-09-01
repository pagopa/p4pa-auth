package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.NotAuthorizedException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class UserUnauthorizedException extends NotAuthorizedException {

    public UserUnauthorizedException(String message){
        super(ErrorCodeConstants.ERROR_CODE_USER_UNAUTHORIZED, message);
    }
}
