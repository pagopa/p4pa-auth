package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ExchangeTokenService exchangeTokenServiceMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private LogoutService logoutServiceMock;

    private AuthService service;

    @BeforeEach
    void init(){
        service = new AuthServiceImpl(exchangeTokenServiceMock, userServiceMock, logoutServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                exchangeTokenServiceMock,
                userServiceMock,
                logoutServiceMock
        );
    }

    @Test
    void whenPostTokenThenCallExchangeService(){
        // Given
        String clientId="CLIENT_ID";
        String grantType="GRANT_TYPE";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        AccessToken expectedResult = new AccessToken();
        Mockito.when(exchangeTokenServiceMock.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedResult);

        // When
        AccessToken result = service.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedResult, result);
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
