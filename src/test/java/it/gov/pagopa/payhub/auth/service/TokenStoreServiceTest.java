package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.AuthenticatedIamUserInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenStoreServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenStoreServiceImpl service;

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
    void givenClaimsWhenSaveRefreshTokenThenReturnThem() {
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        AuthenticatedIamUserInfoDTO refreshUserInfo = new AuthenticatedIamUserInfoDTO(userInfo, accessToken);

        // When
        AuthenticatedIamUserInfoDTO result = service.saveRefreshToken(refreshToken, refreshUserInfo);

        // Then
        Assertions.assertSame(refreshUserInfo, result);
    }

    @Test
    void givenClaimsAndTtlWhenSaveRefreshTokenThenWriteToRedisAndReturnThem() {
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        AuthenticatedIamUserInfoDTO refreshUserInfo = new AuthenticatedIamUserInfoDTO(userInfo, accessToken);
        long ttlInSeconds = 3600L;
        String expectedRedisKey = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        AuthenticatedIamUserInfoDTO result = service.saveRefreshToken(refreshToken, refreshUserInfo, ttlInSeconds);

        // Then
        Assertions.assertSame(refreshUserInfo, result);
        verify(valueOperations).set(expectedRedisKey, refreshUserInfo, Duration.ofSeconds(ttlInSeconds));
    }

    @Test
    void givenRefreshTokenWhenLoadRefreshTokenThenNull(){
        // Given
        String refreshToken = "RefreshToken";

        // When
        AuthenticatedIamUserInfoDTO result = service.loadRefreshToken(refreshToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void givenInvalidRefreshTokenWhenDeleteRefreshTokenThenNull(){
        // Given
        String refreshToken = "RefreshToken";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(null);

        // When
        service.deleteRefreshToken(refreshToken);

        // Then
        verify(redisTemplate, never())
                .delete(anyList());
    }

    @Test
    void givenAccessAndRefreshTokenWhenDeleteRefreshTokenThenNull(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        AuthenticatedIamUserInfoDTO refreshUserInfo = new AuthenticatedIamUserInfoDTO(userInfo, accessToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" +refreshToken)).thenReturn(refreshUserInfo);

        // When
        service.deleteRefreshToken(refreshToken);

        // Then
        verify(redisTemplate, times(1))
                .delete(List.of(
                        RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken,
                        RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + accessToken
                ));
    }
}
