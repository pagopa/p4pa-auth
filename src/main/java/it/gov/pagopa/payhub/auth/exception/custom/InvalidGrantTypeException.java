package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import lombok.Getter;

@Getter
public class InvalidGrantTypeException extends BaseBusinessException {

    public InvalidGrantTypeException(String message) {
        super(ErrorCodeConstants.ERROR_CODE_INVALID_GRANT_TYPE, message);
    }
}
