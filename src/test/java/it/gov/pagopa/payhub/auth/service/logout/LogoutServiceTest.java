package it.gov.pagopa.payhub.auth.service.logout;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    private static final String ACCESS_TOKEN_TYPE = "access_token";
    private static final String REFRESH_TOKEN_TYPE = "refresh_token";

    @Mock
    private ValidateExternalTokenService validateExternalTokenServiceMock;
    @Mock
    private TokenStoreService tokenStoreServiceMock;
    @Mock
    private AuditLoggerService auditServiceMock;

    private MockedStatic<JWT> mockedJWT;

    private LogoutService service;

    @BeforeEach
    void init(){
        service = new LogoutServiceImpl(validateExternalTokenServiceMock, tokenStoreServiceMock, auditServiceMock);
        mockedJWT = mockStatic(JWT.class);
    }

    @AfterEach
    void close() {
        mockedJWT.close();
    }

    @Test
    void givenInvalidClientIdWhenLogoutThenInvalidClientException(){
        // Given
        String clientId = "clientId";
        String token = "token";

        InvalidExchangeClientException expectedException = new InvalidExchangeClientException("ERRORCODE", "");
        Mockito.doThrow(expectedException)
                        .when(validateExternalTokenServiceMock).validateClient(clientId);

        // When, Then
        InvalidExchangeClientException exception = Assertions.assertThrows(InvalidExchangeClientException.class, () -> service.logout(clientId, token));
        Assertions.assertSame(expectedException, exception);
    }

    @Test
    void givenValidClientIdAndAccessTokenWhenLogoutThenOk(){
        // Given
        String clientId = "clientId";
        String accessToken = "accessToken";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim typClaim = mock(Claim.class);
        when(typClaim.asString()).thenReturn(ACCESS_TOKEN_TYPE);
        claimsMap.put("typ", typClaim);

        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);

        mockedJWT.when(() -> JWT.decode(accessToken)).thenReturn(decodedJWTMock);

        // When
        service.logout(clientId, accessToken);

        // Then
        Mockito.verify(validateExternalTokenServiceMock).validateClient(clientId);
        Mockito.verify(tokenStoreServiceMock).delete(accessToken);
    }

    @Test
    void givenValidClientIdAndRefreshTokenWhenLogoutThenOk(){
        // Given
        String clientId = "clientId";
        String refreshToken = "refreshToken";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim typClaim = mock(Claim.class);
        when(typClaim.asString()).thenReturn(REFRESH_TOKEN_TYPE);
        claimsMap.put("typ", typClaim);

        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);

        mockedJWT.when(() -> JWT.decode(refreshToken)).thenReturn(decodedJWTMock);

        // When
        service.logout(clientId, refreshToken);

        // Then
        Mockito.verify(validateExternalTokenServiceMock).validateClient(clientId);
        Mockito.verify(tokenStoreServiceMock).deleteRefreshToken(refreshToken);
    }

    @Test
    void givenInvalidTokenTypeWhenLogoutThenOk(){
        // Given
        String clientId = "clientId";
        String refreshToken = "refreshToken";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim typClaim = mock(Claim.class);
        when(typClaim.asString()).thenReturn("UNKNOWN");
        claimsMap.put("typ", typClaim);

        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);

        mockedJWT.when(() -> JWT.decode(refreshToken)).thenReturn(decodedJWTMock);

        // When
        service.logout(clientId, refreshToken);

        // Then
        Mockito.verify(validateExternalTokenServiceMock).validateClient(clientId);
        Mockito.verify(tokenStoreServiceMock, Mockito.never()).delete(Mockito.anyString());
        Mockito.verify(tokenStoreServiceMock, Mockito.never()).deleteRefreshToken(Mockito.anyString());
    }
}
