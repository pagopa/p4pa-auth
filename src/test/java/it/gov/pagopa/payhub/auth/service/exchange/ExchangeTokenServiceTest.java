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
    private ValidateTokenService validateTokenServiceMock;

    private ExchangeTokenService service;

    @BeforeEach
    void init(){
        service = new ExchangeTokenServiceImpl(validateTokenServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                validateTokenServiceMock
        );
    }

    @Test
    void givenValidTokenWhenPostTokenThenSuccess(){
        // Given
        String token = "TOKEN";

        // When
        service.postToken(token);

        // Then
        Mockito.verify(validateTokenServiceMock).validate(token);
    }
}
