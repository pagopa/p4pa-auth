package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.a2a.AuthorizeClientCredentialsRequestService;
import it.gov.pagopa.payhub.auth.service.a2a.ClientService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class AuthorizeClientCredentialsRequestServiceTest {

	@Mock
	private ClientService clientService;
	private AuthorizeClientCredentialsRequestService service;

	@BeforeEach
	void init() {
		service = new AuthorizeClientCredentialsRequestService(clientService);
	}

	@Test
	void givenValidTokenWhenPostTokenThenSuccess(){
		// Given
		String clientId="CLIENT_ID";
		String clientSecret="CLIENT_SECRET";
		ClientDTO expectedClientDTO = new ClientDTO();

		Mockito.doReturn(expectedClientDTO).when(clientService).authorizeCredentials(clientId, clientSecret);
		//When
		ClientDTO result = service.authorizeCredentials(clientId, clientSecret);
		//Then
		Assertions.assertEquals(expectedClientDTO, result);
	}
}
