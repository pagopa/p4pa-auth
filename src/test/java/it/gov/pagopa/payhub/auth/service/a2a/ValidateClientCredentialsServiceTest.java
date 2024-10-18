package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeRequestException;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidateClientCredentialsServiceTest {

	@Mock
	private ClientService clientService;
	private ValidateClientCredentialsService service;

	private static final String ALLOWED_CLIENT_ID = "CLIENTID";
	private static final String ALLOWED_CLIENT_SECRET = "CLIENTSECRET";

	@BeforeEach
	void setup(){
		service = new ValidateClientCredentialsService(clientService);
	}

	@Test
	void givenValidRequestThenOk() {
		Mockito.doReturn(new ClientDTO()).when(clientService).verifyCredentials(ALLOWED_CLIENT_ID, ALLOWED_CLIENT_SECRET);
		assertDoesNotThrow(() ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidClientIdThenInvalidExchangeRequestException() {
		Mockito.doThrow(new InvalidExchangeRequestException("error"))
			.when(clientService).verifyCredentials("UNEXPECTED_CLIENT_ID", ALLOWED_CLIENT_SECRET);
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate("UNEXPECTED_CLIENT_ID", ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidScopeThenInvalidExchangeRequestException() {
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, "UNEXPECTED_SCOPE", ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenNullClientSecretThenInvalidExchangeRequestException() {
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_SCOPE, null));
	}

	@Test
	void givenInvalidClientSecretThenInvalidExchangeRequestException() {
		Mockito.doThrow(new InvalidExchangeRequestException("error"))
			.when(clientService).verifyCredentials(ALLOWED_CLIENT_ID, "UNEXPECTED_CLIENT_SECRET");
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_SCOPE, "UNEXPECTED_CLIENT_SECRET"));
	}

}
