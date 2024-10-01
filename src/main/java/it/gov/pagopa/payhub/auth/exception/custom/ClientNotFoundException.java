package it.gov.pagopa.payhub.auth.exception.custom;

public class ClientNotFoundException extends RuntimeException {

	public ClientNotFoundException(String message) {
		super(message);
	}
}
