package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class OperatorNotFoundException extends BaseBusinessException {

    public OperatorNotFoundException(String message) {
        super(ErrorCodeConstants.ERROR_CODE_OPERATOR_NOT_FOUND, message);
    }
}
