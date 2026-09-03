package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private TokenStoreService tokenStoreService;

    @Mock
    private AccessTokenBuilderService accessTokenBuilderService;

    @Mock
    private ValidateRefreshTokenService validateRefreshTokenService;

    private RefreshTokenServiceImpl refreshTokenService;

    private final String clientId = "piattaforma-unitaria";
    private final String refreshToken = "valid-refresh-token-string";

    private IamUserInfoDTO mockUserInfo;
    private AccessToken mockAccessToken;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(
                tokenStoreService,
                accessTokenBuilderService,
                validateRefreshTokenService,
                86400
        );

        mockUserInfo = new IamUserInfoDTO();
        mockAccessToken = new AccessToken();
        mockAccessToken.setAccessToken("new-access-token");
        mockAccessToken.setRefreshToken("new-refresh-token");
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        verifyNoMoreInteractions(
                tokenStoreService,
                accessTokenBuilderService,
                validateRefreshTokenService
        );
    }

    @Test
    void givenValidRefreshTokenWhenRefreshTokenThenSuccess() {
        // Given
        long nowSeconds = Instant.now().getEpochSecond();
        long sessionIssuedAt = nowSeconds - 3600;
        mockUserInfo.setIssuedAt(sessionIssuedAt);

        when(tokenStoreService.loadRefreshToken(refreshToken)).thenReturn(mockUserInfo);
        when(accessTokenBuilderService.build(eq(mockUserInfo), eq(null), anyInt(), eq(true)))
                .thenReturn(mockAccessToken);

        // When
        AccessToken result = refreshTokenService.refreshToken(clientId, refreshToken);

        // Then
        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());

        verify(validateRefreshTokenService).validate(clientId, refreshToken);
        verify(tokenStoreService).deleteRefreshToken(refreshToken);

        verify(accessTokenBuilderService).build(eq(mockUserInfo), eq(null), argThat(ttl -> ttl >= 82790 && ttl <= 82800), eq(true));
        verify(tokenStoreService).save("new-access-token", mockUserInfo);
        verify(tokenStoreService).saveRefreshToken(eq("new-refresh-token"), eq(mockUserInfo), longThat(ttl -> ttl >= 82790 && ttl <= 82800));
    }

    @Test
    void givenSessionExpiredWhenRefreshTokenThenThrowsInvalidAccessTokenException() {
        // Given
        long nowSeconds = Instant.now().getEpochSecond();
        long sessionIssuedAt = nowSeconds - 90000;
        mockUserInfo.setIssuedAt(sessionIssuedAt);

        when(tokenStoreService.loadRefreshToken(refreshToken)).thenReturn(mockUserInfo);

        // When & Then
        InvalidAccessTokenException exception = assertThrows(InvalidAccessTokenException.class, () ->
                refreshTokenService.refreshToken(clientId, refreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, exception.getCode());
        assertTrue(exception.getMessage().contains("Session expired, re-authentication required"));

        verify(validateRefreshTokenService).validate(clientId, refreshToken);
        verify(tokenStoreService).deleteRefreshToken(refreshToken);
    }

    @Test
    void givenEmptyRefreshTokenWhenRefreshTokenThenThrowsInvalidTokenException() {
        // Given
        String emptyRefreshToken = "";

        // When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                refreshTokenService.refreshToken(clientId, emptyRefreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, exception.getCode());
        assertTrue(exception.getMessage().contains("Missing refresh_token parameter"));
    }

    @Test
    void givenTokenNotFoundInStoreWhenRefreshTokenThenThrowsInvalidAccessTokenException() {
        // Given
        when(tokenStoreService.loadRefreshToken(refreshToken)).thenReturn(null);

        // When & Then
        InvalidAccessTokenException exception = assertThrows(InvalidAccessTokenException.class, () ->
                refreshTokenService.refreshToken(clientId, refreshToken)
        );

        assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, exception.getCode());
        assertTrue(exception.getMessage().contains("RefreshToken not found"));

        verify(validateRefreshTokenService).validate(clientId, refreshToken);
    }

    @Test
    void givenInvalidRefreshTokenWhenRefreshTokenThenThrowsException() {
        // Given
        doThrow(new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "Validation failed"))
                .when(validateRefreshTokenService).validate(clientId, refreshToken);

        // When & Then
        assertThrows(InvalidTokenException.class, () ->
                refreshTokenService.refreshToken(clientId, refreshToken)
        );
    }

    @Test
    void givenRefreshTokenWithNullIssueAtWhenRefreshTokenThenSetIssueAtAndSuccess() {
        // Given
        mockUserInfo.setIssuedAt(null);

        when(tokenStoreService.loadRefreshToken(refreshToken)).thenReturn(mockUserInfo);
        when(accessTokenBuilderService.build(eq(mockUserInfo), eq(null), anyInt(), eq(true)))
                .thenReturn(mockAccessToken);

        // When
        AccessToken result = refreshTokenService.refreshToken(clientId, refreshToken);

        // Then
        assertNotNull(result);
        assertNotNull(mockUserInfo.getIssuedAt());
        assertTrue(mockUserInfo.getIssuedAt() > 0);

        verify(validateRefreshTokenService).validate(clientId, refreshToken);
        verify(tokenStoreService).deleteRefreshToken(refreshToken);

        verify(accessTokenBuilderService).build(eq(mockUserInfo), eq(null), argThat(ttl -> ttl >= 86390 && ttl <= 86400), eq(true));
        verify(tokenStoreService).save("new-access-token", mockUserInfo);
        verify(tokenStoreService).saveRefreshToken(eq("new-refresh-token"), eq(mockUserInfo), longThat(ttl -> ttl >= 86390 && ttl <= 86400));
    }
}