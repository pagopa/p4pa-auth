package it.gov.pagopa.payhub.auth.service.logout;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private ValidateExternalTokenService validateExternalTokenServiceMock;
    @Mock
    private TokenStoreService tokenStoreServiceMock;

    private LogoutService service;

    @BeforeEach
    void init(){
        service = new LogoutServiceImpl(validateExternalTokenServiceMock, tokenStoreServiceMock);
    }

    @Test
    void givenInvalidClientIdWhenLogoutThenInvalidClientException(){
        // Given
        String clientId = "clientId";
        String token = "token";

        InvalidExchangeClientException expectedException = new InvalidExchangeClientException("");
        Mockito.doThrow(expectedException)
                        .when(validateExternalTokenServiceMock).validateClient(clientId);

        // When, Then
        InvalidExchangeClientException exception = Assertions.assertThrows(InvalidExchangeClientException.class, () -> service.logout(clientId, token));
        Assertions.assertSame(expectedException, exception);
    }

    @Test
    void givenCompleteRequestWhenLogoutThenOk(){
        // Given
        String clientId = "clientId";
        String token = "token";

        // When
        service.logout(clientId, token);

        // Then
        Mockito.verify(validateExternalTokenServiceMock).validateClient(clientId);
        Mockito.verify(tokenStoreServiceMock).delete(token);
    }
}
