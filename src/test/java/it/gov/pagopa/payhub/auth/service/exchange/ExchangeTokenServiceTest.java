package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenServiceTest {

    @Mock
    private ValidateExternalTokenService validateExternalTokenServiceMock;
    @Mock
    private AccessTokenBuilderService accessTokenBuilderServiceMock;
    @Mock
    private TokenStoreService tokenStoreServiceMock;

    private ExchangeTokenService service;

    @BeforeEach
    void init(){
        service = new ExchangeTokenServiceImpl(validateExternalTokenServiceMock, accessTokenBuilderServiceMock, tokenStoreServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                validateExternalTokenServiceMock,
                accessTokenBuilderServiceMock,
                tokenStoreServiceMock
        );
    }

    @Test
    void givenValidTokenWhenPostTokenThenSuccess(){
        // Given
        String clientId="CLIENT_ID";
        String grantType="GRANT_TYPE";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        HashMap<String, String> expectedClaims = new HashMap<>();
        Mockito.when(validateExternalTokenServiceMock.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedClaims);

        AccessToken expectedAccessToken = new AccessToken();
        Mockito.when(accessTokenBuilderServiceMock.build())
                .thenReturn(expectedAccessToken);

        // When
        AccessToken result = service.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken), Mockito.same(expectedClaims));
    }
}
