package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;

public class ClientNotFoundException extends BaseBusinessException {

	public ClientNotFoundException(String message) {
		super(ErrorCodeConstants.ERROR_CODE_CLIENT_NOT_FOUND, message);
	}
}
