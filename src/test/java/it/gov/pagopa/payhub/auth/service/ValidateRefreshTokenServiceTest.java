package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateRefreshTokenServiceTest {

    private static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    private static final String ALLOWED_AUDIENCE = "https://allowed-audience.pagopa.it";
    private static final String REFRESH_TOKEN_TYPE = "refresh_token";

    private ValidateRefreshTokenService validateRefreshTokenService;
    private MockedStatic<JWT> mockedJWT;
    @Mock
    private JWTValidator jwtValidator;

    @BeforeEach
    void setUp() {
        validateRefreshTokenService = new ValidateRefreshTokenService(ALLOWED_AUDIENCE, jwtValidator);
        mockedJWT = mockStatic(JWT.class);
    }

    @AfterEach
    void close() {
        mockedJWT.close();
    }

    @Test
    void givenValidParametersWhenValidateThenReturnsClaims() {
        // Given
        String refreshToken = "refresh-token";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim issClaim = mock(Claim.class);
        when(issClaim.asString()).thenReturn(ALLOWED_AUDIENCE);
        Claim typClaim = mock(Claim.class);
        when(typClaim.asString()).thenReturn(REFRESH_TOKEN_TYPE);

        claimsMap.put("iss", issClaim);
        claimsMap.put("typ", typClaim);

        mockedJWT.when(() -> JWT.decode(refreshToken)).thenReturn(decodedJWTMock);
        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);
        doNothing().when(jwtValidator).validateInternalToken(refreshToken);

        // When
        Map<String, Claim> result = validateRefreshTokenService.validate(ALLOWED_CLIENT_ID, refreshToken);

        // Then
        assertNotNull(result);
        assertEquals(ALLOWED_AUDIENCE, result.get("iss").asString());
        assertEquals(REFRESH_TOKEN_TYPE, result.get("typ").asString());
        Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(refreshToken));
        mockedJWT.verify(() -> JWT.decode(refreshToken));
    }

    @Test
    void givenInvalidClientIdWhenValidateThenThrowsInvalidExchangeClientException() {
        // Given
        String invalidClientId = "invalid-client";
        String refreshToken = "token";

        // When & Then
        InvalidExchangeClientException exception = assertThrows(InvalidExchangeClientException.class, () ->
                validateRefreshTokenService.validate(invalidClientId, refreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_CLIENT_ID, exception.getCode());
    }

    @Test
    void givenEmptyRefreshTokenWhenValidateThenThrowsInvalidTokenException() {
        // Given
        String emptyRefreshToken = "";

        // When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                validateRefreshTokenService.validate(ALLOWED_CLIENT_ID, emptyRefreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, exception.getCode());
    }

    @Test
    void givenInvalidIssuerWhenValidateThenThrowsInvalidTokenException() {
        // Given
        String refreshToken = "refresh-token";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim issClaim = mock(Claim.class);
        when(issClaim.asString()).thenReturn("https://wrong-issuer.com");
        claimsMap.put("iss", issClaim);

        mockedJWT.when(() -> JWT.decode(refreshToken)).thenReturn(decodedJWTMock);
        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);

        // When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                validateRefreshTokenService.validate(ALLOWED_CLIENT_ID, refreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_ISSUER, exception.getCode());
        mockedJWT.verify(() -> JWT.decode(refreshToken));
    }

    @Test
    void givenWrongTokenTypeWhenValidateThenThrowsInvalidTokenException() {
        // Given
        String refreshToken = "refresh-token";
        DecodedJWT decodedJWTMock = mock(DecodedJWT.class);

        Map<String, Claim> claimsMap = new HashMap<>();
        Claim issClaim = mock(Claim.class);
        when(issClaim.asString()).thenReturn(ALLOWED_AUDIENCE);
        Claim typClaim = mock(Claim.class);
        when(typClaim.asString()).thenReturn("wrong_type");

        claimsMap.put("iss", issClaim);
        claimsMap.put("typ", typClaim);

        mockedJWT.when(() -> JWT.decode(refreshToken)).thenReturn(decodedJWTMock);
        when(decodedJWTMock.getClaims()).thenReturn(claimsMap);

        // When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                validateRefreshTokenService.validate(ALLOWED_CLIENT_ID, refreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN_TYPE, exception.getCode());
        mockedJWT.verify(() -> JWT.decode(refreshToken));
    }
}

