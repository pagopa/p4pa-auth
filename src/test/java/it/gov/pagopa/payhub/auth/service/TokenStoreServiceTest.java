package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        String refreshToken = "RefreshToken";

        // When
        IamUserInfoDTO result = service.saveRefreshToken(refreshToken, userInfo);

        // Then
        Assertions.assertSame(userInfo, result);
    }

    @Test
    void givenClaimsAndTtlWhenSaveRefreshTokenThenWriteToRedisAndReturnThem() {
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String refreshToken = "RefreshToken";
        long ttlInSeconds = 3600L;
        String expectedRedisKey = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        IamUserInfoDTO result = service.saveRefreshToken(refreshToken, userInfo, ttlInSeconds);

        // Then
        Assertions.assertSame(userInfo, result);
        verify(valueOperations).set(expectedRedisKey, userInfo, Duration.ofSeconds(ttlInSeconds));
    }

    @Test
    void givenRefreshTokenWhenLoadRefreshTokenThenNull(){
        // Given
        String refreshToken = "RefreshToken";

        // When
        IamUserInfoDTO result = service.loadRefreshToken(refreshToken);

        // Then
        Assertions.assertNull(result);
    }
}
