package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeRequestException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidateClientCredentialsServiceTest {
	@Mock
	private ClientService clientServiceMock;
	private ValidateClientCredentialsService service;

	private static final String ALLOWED_CLIENT_ID = "CLIENTID";
	private static final String ALLOWED_CLIENT_SECRET = "CLIENTSECRET";

	@BeforeEach
	void setup(){
		service = new ValidateClientCredentialsService(clientServiceMock);
	}

	@Test
	void givenValidRequestThenOk() {
		Mockito.doReturn(Optional.of(new Client())).when(clientServiceMock).getClientByClientId(ALLOWED_CLIENT_ID);
		assertDoesNotThrow(() ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_GRANT_TYPE, ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidExchangeClientException() {
		assertThrows(InvalidExchangeClientException.class, () ->
			service.validate("UNEXPECTED_CLIENT_ID", ValidateClientCredentialsService.ALLOWED_GRANT_TYPE, ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidGrantTypeException() {
		Mockito.doReturn(Optional.of(new Client())).when(clientServiceMock).getClientByClientId(ALLOWED_CLIENT_ID);
		assertThrows(InvalidGrantTypeException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, "UNEXPECTED_GRANT_TYPE", ValidateClientCredentialsService.ALLOWED_SCOPE, ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenInvalidScopeThenInvalidExchangeRequestException() {
		Mockito.doReturn(Optional.of(new Client())).when(clientServiceMock).getClientByClientId(ALLOWED_CLIENT_ID);
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_GRANT_TYPE, "UNEXPECTED_SCOPE", ALLOWED_CLIENT_SECRET));
	}

	@Test
	void givenNullClientSecretThenInvalidExchangeRequestException() {
		Mockito.doReturn(Optional.of(new Client())).when(clientServiceMock).getClientByClientId(ALLOWED_CLIENT_ID);
		assertThrows(InvalidExchangeRequestException.class, () ->
			service.validate(ALLOWED_CLIENT_ID, ValidateClientCredentialsService.ALLOWED_GRANT_TYPE, ValidateClientCredentialsService.ALLOWED_SCOPE, null));
	}

}
