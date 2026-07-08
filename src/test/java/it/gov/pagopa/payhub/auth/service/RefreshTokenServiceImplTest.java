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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private final String clientId = "piattaforma-unitaria";
    private final String refreshToken = "valid-refresh-token-string";
    private IamUserInfoDTO mockUserInfo;
    private AccessToken mockAccessToken;

    @BeforeEach
    void setUp() {
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
        when(tokenStoreService.loadRefreshToken(refreshToken)).thenReturn(mockUserInfo);
        when(accessTokenBuilderService.build(mockUserInfo)).thenReturn(mockAccessToken);

        // When
        AccessToken result = refreshTokenService.refreshToken(clientId, refreshToken);

        // Then
        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());

        verify(validateRefreshTokenService).validate(clientId, refreshToken);
        verify(tokenStoreService).deleteRefreshToken(refreshToken);
        verify(tokenStoreService).save("new-access-token", mockUserInfo);
        verify(tokenStoreService).saveRefreshToken("new-refresh-token", mockUserInfo);
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

}