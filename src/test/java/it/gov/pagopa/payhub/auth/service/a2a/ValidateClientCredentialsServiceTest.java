package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidateClientCredentialsServiceTest {


	@InjectMocks
	private ValidateClientCredentialsService service;

	private static final String ALLOWED_CLIENT_SECRET = "CLIENTSECRET";

	@Test
	void givenValidRequestThenOk() {
		assertDoesNotThrow(() ->
			service.validate(ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidScopeThenInvalidExchangeRequestException() {
		assertThrows(ClientUnauthorizedException.class, () ->
			service.validate( "UNEXPECTED_SCOPE", ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenNullClientSecretThenInvalidExchangeRequestException() {
		assertThrows(ClientUnauthorizedException.class, () ->
			service.validate(ValidateClientCredentialsService.ALLOWED_SCOPE, null));
	}

}
