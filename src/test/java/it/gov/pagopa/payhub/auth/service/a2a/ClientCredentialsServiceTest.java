package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.AccessToken;
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
	private ClientCredentialService service;

	@BeforeEach
	void init() {
		service = new ClientCredentialServiceImpl(validateClientCredentialsServiceMock);
	}

	@Test
	void givenValidTokenWhenPostTokenThenSuccess(){
		// Given
		String clientId="CLIENT_ID";
		String scope="SCOPE";
		String clientSecret="CLIENT_SECRET";

		Mockito.doNothing().when(validateClientCredentialsServiceMock).validate(clientId, scope, clientSecret);
		AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
		//When
		AccessToken result = service.postToken(clientId, scope, clientSecret);
		//Then
		Assertions.assertEquals(expectedAccessToken, result);
	}

}
