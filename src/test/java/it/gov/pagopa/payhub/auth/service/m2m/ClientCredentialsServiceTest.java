package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.mapper.Client2UserInfoMapper;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
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
	private AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestServiceMock;
	@Mock
	private AccessTokenBuilderService accessTokenBuilderServiceMock;
	@Mock
	private TokenStoreService tokenStoreServiceMock;
	@Mock
	private Client2UserInfoMapper client2UserInfoMapperMock;

	private ClientCredentialService service;

	@BeforeEach
	void init() {
		service = new ClientCredentialServiceImpl(
			validateClientCredentialsServiceMock,
			authorizeClientCredentialsRequestServiceMock,
			accessTokenBuilderServiceMock,
			tokenStoreServiceMock,
			client2UserInfoMapperMock
		);
	}

	@Test
	void givenValidTokenWhenPostTokenThenSuccess(){
		// Given
		String clientId="CLIENT_ID";
		String scope="SCOPE";
		String clientSecret="CLIENT_SECRET";

		Mockito.doNothing().when(validateClientCredentialsServiceMock).validate(scope, clientSecret);
		ClientNoSecretDTO clientDTO = ClientNoSecretDTO.builder()
				.organizationIpaCode("ORGIPACODE")
				.build();
		Mockito.doReturn(clientDTO).when(authorizeClientCredentialsRequestServiceMock).authorizeCredentials(clientId, clientSecret);
		IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
		Mockito.when(client2UserInfoMapperMock.apply(clientDTO)).thenReturn(iamUserInfo);
		AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
		Mockito.when(accessTokenBuilderServiceMock.build(iamUserInfo)).thenReturn(expectedAccessToken);
		//When
		AccessToken result = service.postToken(clientId, scope, clientSecret);
		//Then
		Assertions.assertSame(expectedAccessToken, result);
		Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
	}

}
