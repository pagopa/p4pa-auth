package it.gov.pagopa.payhub.auth.service.exchange;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenServiceTest {

    @Mock
    private ValidateExternalTokenService validateExternalTokenServiceMock;

    private ExchangeTokenService service;

    @BeforeEach
    void init(){
        service = new ExchangeTokenServiceImpl(validateExternalTokenServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                validateExternalTokenServiceMock
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

        // When
        service.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Mockito.verify(validateExternalTokenServiceMock).validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }
}
