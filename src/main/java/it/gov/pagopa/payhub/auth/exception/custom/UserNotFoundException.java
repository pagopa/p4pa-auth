package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class UserNotFoundException extends BaseBusinessException {

    public UserNotFoundException(String message) {
        super(ErrorCodeConstants.ERROR_CODE_USER_NOT_FOUND, message);
    }
}
