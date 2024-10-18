package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientCredentialsServiceTest {

	@Mock
	private ValidateClientCredentialsService validateClientCredentialsServiceMock;
	@Mock
	private AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService;

	private ClientCredentialService service;

	@BeforeEach
	void init() {
		service = new ClientCredentialServiceImpl(validateClientCredentialsServiceMock, authorizeClientCredentialsRequestService);
	}

	@Test
	void givenValidTokenWhenPostTokenThenSuccess(){
		// Given
		String clientId="CLIENT_ID";
		String scope="SCOPE";
		String clientSecret="CLIENT_SECRET";

		Mockito.doNothing().when(validateClientCredentialsServiceMock).validate(scope, clientSecret);
		Mockito.doReturn(new ClientDTO()).when(authorizeClientCredentialsRequestService).authorizeCredentials(clientId, clientSecret);
		AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
		//When
		AccessToken result = service.postToken(clientId, scope, clientSecret);
		//Then
		Assertions.assertEquals(expectedAccessToken, result);
	}

}
