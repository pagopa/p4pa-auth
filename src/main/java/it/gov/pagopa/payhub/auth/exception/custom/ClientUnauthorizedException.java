package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class ClientUnauthorizedException extends BaseBusinessException {

    public ClientUnauthorizedException(String message){
        super(ErrorCodeConstants.ERROR_CODE_CLIENT_UNAUTHORIZED, message);
    }
}
