package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
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

    private AuthService service;

    @BeforeEach
    void init(){
        service = new AuthServiceImpl(exchangeTokenServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                exchangeTokenServiceMock
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
}
