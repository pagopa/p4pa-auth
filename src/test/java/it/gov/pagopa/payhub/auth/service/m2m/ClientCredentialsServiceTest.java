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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

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

		doNothing().when(validateClientCredentialsServiceMock).validate(scope, clientSecret);
		ClientNoSecretDTO clientDTO = ClientNoSecretDTO.builder()
				.organizationIpaCode("ORGIPACODE")
				.build();
		doReturn(clientDTO).when(authorizeClientCredentialsRequestServiceMock).authorizeCredentials(clientId, clientSecret);
		IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
		when(client2UserInfoMapperMock.apply(clientDTO)).thenReturn(iamUserInfo);
		AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
		when(accessTokenBuilderServiceMock.build(iamUserInfo, null, false)).thenReturn(expectedAccessToken);
		//When
		AccessToken result = service.postToken(clientId, scope, clientSecret);
		//Then
		Assertions.assertSame(expectedAccessToken, result);
		verify(tokenStoreServiceMock).save(same(expectedAccessToken.getAccessToken()), same(iamUserInfo));
	}

}
