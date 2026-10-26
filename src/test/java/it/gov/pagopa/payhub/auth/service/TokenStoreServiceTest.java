package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.RefreshTokenSessionDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenStoreServiceTest {

    @Mock
    private RedisTemplate<String, RefreshTokenSessionDTO> refreshTokenRedisTemplateMock;

    @Mock
    private StringRedisTemplate tokenInvalidationRedisTemplateMock;

    @Mock
    private ValueOperations<String, RefreshTokenSessionDTO> valueOperations;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private TokenStoreServiceImpl service;

    @BeforeEach
    void setup() {
        service = new TokenStoreServiceImpl(refreshTokenRedisTemplateMock, tokenInvalidationRedisTemplateMock);
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void close() {
        mockedSecurityUtils.close();
    }

    @Test
    void givenClaimsWhenSaveThenReturnThem(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";

        // When
        IamUserInfoDTO result = service.save(accessToken, userInfo);

        // Then
        Assertions.assertSame(userInfo, result);
    }

    @Test
    void givenAccessTokenWhenLoadThenNull(){
        // Given
        String accessToken = "AccessToken";

        // When
        IamUserInfoDTO result = service.load(accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenNullAccessTokenWhenDeleteThenNull(){
        // Given
        String accessToken = "AccessToken";

        mockedSecurityUtils.when(SecurityUtils::getAccessToken).thenReturn(null);

        // When
        service.delete(accessToken);

        // Then
        verify(tokenInvalidationRedisTemplateMock, times(1))
                .delete(RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + accessToken);
    }

    @Test
    void givenDifferentAccessTokenWhenDeleteThenNull(){
        // Given
        String accessToken = "AccessToken";
        String differentAccessToken = "differentAccessToken";

        mockedSecurityUtils.when(SecurityUtils::getAccessToken).thenReturn(differentAccessToken);

        // When
        InvalidAccessTokenException invalidAccessTokenException = Assertions.assertThrows(InvalidAccessTokenException.class, () -> service.delete(accessToken));

        // Then
        Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, invalidAccessTokenException.getCode());
        Assertions.assertEquals("AccessToken to delete is not the same AccessToken provided as authentication", invalidAccessTokenException.getMessage());
        verify(tokenInvalidationRedisTemplateMock, never())
                .delete(Mockito.anyString());
    }

    @Test
    void givenAccessTokenWhenDeleteThenNull(){
        // Given
        String accessToken = "AccessToken";

        mockedSecurityUtils.when(SecurityUtils::getAccessToken).thenReturn(accessToken);

        // When
        service.delete(accessToken);

        // Then
        verify(tokenInvalidationRedisTemplateMock, times(1))
                .delete(RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + accessToken);
    }

    @Test
    void givenClaimsWhenSaveRefreshTokenThenReturnThem() {
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        RefreshTokenSessionDTO refreshUserInfo = new RefreshTokenSessionDTO(userInfo, accessToken);

        // When
        RefreshTokenSessionDTO result = service.saveRefreshToken(refreshToken, refreshUserInfo);

        // Then
        Assertions.assertSame(refreshUserInfo, result);
    }

    @Test
    void givenClaimsAndTtlWhenSaveRefreshTokenThenWriteToRedisAndReturnThem() {
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        RefreshTokenSessionDTO refreshUserInfo = new RefreshTokenSessionDTO(userInfo, accessToken);
        long ttlInSeconds = 3600L;
        String expectedRedisKey = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;

        when(refreshTokenRedisTemplateMock.opsForValue()).thenReturn(valueOperations);

        // When
        RefreshTokenSessionDTO result = service.saveRefreshToken(refreshToken, refreshUserInfo, ttlInSeconds);

        // Then
        Assertions.assertSame(refreshUserInfo, result);
        verify(valueOperations).set(expectedRedisKey, refreshUserInfo, Duration.ofSeconds(ttlInSeconds));
    }

    @Test
    void givenRefreshTokenWhenLoadRefreshTokenThenNull(){
        // Given
        String refreshToken = "RefreshToken";

        // When
        RefreshTokenSessionDTO result = service.loadRefreshToken(refreshToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenNullRefreshTokenWhenDeleteRefreshTokenThenDoNothing(){
        // Given
        String refreshToken = "RefreshToken";

        when(refreshTokenRedisTemplateMock.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(null);

        // When
        service.deleteRefreshToken(refreshToken);

        // Then
        verify(tokenInvalidationRedisTemplateMock, never())
                .delete(anyList());
    }

    @Test
    void givenNullAccessTokenWhenDeleteRefreshTokenThenNull(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        RefreshTokenSessionDTO refreshUserInfo = new RefreshTokenSessionDTO(userInfo, accessToken);

        when(refreshTokenRedisTemplateMock.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(refreshUserInfo);

        // When
        service.deleteRefreshToken(refreshToken);

        // Then
        verify(tokenInvalidationRedisTemplateMock, times(1))
                .delete(List.of(
                        RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken,
                        RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + accessToken
                ));
    }

    @Test
    void givenNotAssociatedAccessAndRefreshTokenWhenDeleteRefreshTokenThenNull(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String differentAccessToken = "differentAccessToken";
        String refreshToken = "RefreshToken";
        RefreshTokenSessionDTO refreshUserInfo = new RefreshTokenSessionDTO(userInfo, accessToken);

        when(refreshTokenRedisTemplateMock.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(refreshUserInfo);
        mockedSecurityUtils.when(SecurityUtils::getAccessToken).thenReturn(differentAccessToken);

        // When
        InvalidTokenException invalidTokenException = Assertions.assertThrows(InvalidTokenException.class, () -> service.deleteRefreshToken(refreshToken));

        // Then
        Assertions.assertEquals(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, invalidTokenException.getCode());
        Assertions.assertEquals("RefreshToken to delete is not associated with AccessToken provided as authentication", invalidTokenException.getMessage());
        verify(tokenInvalidationRedisTemplateMock, never())
                .delete(anyList());
    }

    @Test
    void givenAccessAndRefreshTokenWhenDeleteRefreshTokenThenNull(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        RefreshTokenSessionDTO refreshUserInfo = new RefreshTokenSessionDTO(userInfo, accessToken);

        when(refreshTokenRedisTemplateMock.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(refreshUserInfo);
        mockedSecurityUtils.when(SecurityUtils::getAccessToken).thenReturn(accessToken);

        // When
        service.deleteRefreshToken(refreshToken);

        // Then
        verify(tokenInvalidationRedisTemplateMock, times(1))
                .delete(List.of(
                        RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken,
                        RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + accessToken
                ));
    }
}
