package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class M2MClientConflictException extends BaseBusinessException {

	public M2MClientConflictException(String message) {
		super(ErrorCodeConstants.ERROR_CODE_CLIENT_CONFLICT, message);
	}
}
