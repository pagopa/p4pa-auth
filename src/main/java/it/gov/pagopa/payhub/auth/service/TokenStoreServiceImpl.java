package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.RefreshTokenSessionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@CacheConfig(cacheNames = RedisConfig.CACHE_NAME_ACCESS_TOKEN, cacheManager = "redisCacheManager")
class TokenStoreServiceImpl implements  TokenStoreService{
    private final StringRedisTemplate tokenInvalidationRedisTemplate;
    private final RedisTemplate<String, Object> refreshTokenRedisTemplate;

    public TokenStoreServiceImpl(@Qualifier("refreshTokenRedisTemplate") RedisTemplate<String, Object> refreshTokenRedisTemplate,
                                 StringRedisTemplate tokenInvalidationRedisTemplate) {
        this.refreshTokenRedisTemplate = refreshTokenRedisTemplate;
        this.tokenInvalidationRedisTemplate = tokenInvalidationRedisTemplate;
    }

    @Override
    @CachePut(key = "#accessToken")
    public IamUserInfoDTO save(String accessToken, IamUserInfoDTO idTokenClaims) {
        return idTokenClaims;
    }

    @Override
    @Cacheable(unless="#result == null")
    public IamUserInfoDTO load(String accessToken) {
        return null;
    }

    @Override
    @CacheEvict
    public void delete(String accessToken) {
        //Do Nothing
    }

    @Override
    @CachePut(cacheNames = RedisConfig.CACHE_NAME_REFRESH_TOKEN, key = "#refreshToken")
    public RefreshTokenSessionDTO saveRefreshToken(String refreshToken, RefreshTokenSessionDTO refreshTokenSession) {
        return refreshTokenSession;
    }

    @Override
    public RefreshTokenSessionDTO saveRefreshToken(String refreshToken, RefreshTokenSessionDTO refreshTokenSession, long ttlInSeconds) {
        String key = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;
        refreshTokenRedisTemplate.opsForValue().set(key, refreshTokenSession, Duration.ofSeconds(ttlInSeconds));
        return refreshTokenSession;
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_NAME_REFRESH_TOKEN, unless="#result == null")
    public RefreshTokenSessionDTO loadRefreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        String refreshTokenKey = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;
        RefreshTokenSessionDTO authenticatedIamUserInfoDTO = (RefreshTokenSessionDTO) refreshTokenRedisTemplate.opsForValue().get(refreshTokenKey);
        if (authenticatedIamUserInfoDTO ==null)
            return;
        String accessTokenKey = RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + authenticatedIamUserInfoDTO.getAccessToken();
        tokenInvalidationRedisTemplate.delete(List.of(
                refreshTokenKey,
                accessTokenKey
        ));
    }
}
