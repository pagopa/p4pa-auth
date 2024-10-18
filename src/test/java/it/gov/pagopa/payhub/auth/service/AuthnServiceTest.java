package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.service.a2a.ClientCredentialService;
import it.gov.pagopa.payhub.auth.service.a2a.ValidateClientCredentialsService;
import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthnServiceTest {

    @Mock
    private ClientCredentialService clientCredentialService;
    @Mock
    private ExchangeTokenService exchangeTokenServiceMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private LogoutService logoutServiceMock;

    private AuthnService service;

    @BeforeEach
    void init(){
        service = new AuthnServiceImpl(clientCredentialService, exchangeTokenServiceMock, userServiceMock, logoutServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
          clientCredentialService,
          exchangeTokenServiceMock,
          userServiceMock,
          logoutServiceMock
        );
    }

    @Test
    void whenPostTokenThenCallExchangeService(){
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
				String clientSecret = "CLIENT_SECRET";

        String grantType= ValidateExternalTokenService.ALLOWED_GRANT_TYPE;
        AccessToken expectedResult = new AccessToken();
        Mockito.when(exchangeTokenServiceMock.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedResult);

        // When
	      AccessToken result = service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenPostTokenThenCallClientCredentialService(){
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
        String clientSecret = "CLIENT_SECRET";

        String grantType= ValidateClientCredentialsService.ALLOWED_GRANT_TYPE;
        AccessToken expectedResult = new AccessToken();
        Mockito.when(clientCredentialService.postToken(clientId, scope, clientSecret)).thenReturn(expectedResult);

        // When
        AccessToken result = service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenPostTokenWhenCallClientCredentialServiceThenInvalidGrantTypeException(){
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
        String clientSecret = "CLIENT_SECRET";

        String grantType="UNEXPECTED_GRANT_TYPE";
        // When, Then
        assertThrows(InvalidGrantTypeException.class, () ->
            service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret));
    }

    @Test
    void whenGetUserInfoThenCallUserService(){
        // Given
        String accessToken = "accessToken";
        UserInfo expectedResult = new UserInfo();
        Mockito.when(userServiceMock.getUserInfo(accessToken))
                .thenReturn(expectedResult);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenLogoutThenCallLogout(){
        // Given
        String clientId = "clientId";
        String accessToken = "accessToken";

        // When
        service.logout(clientId, accessToken);

        // Then
        Mockito.verify(logoutServiceMock).logout(clientId, accessToken);
    }
}
