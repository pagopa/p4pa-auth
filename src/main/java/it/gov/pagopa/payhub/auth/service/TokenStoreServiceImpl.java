package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.AuthenticatedIamUserInfoDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@CacheConfig(cacheNames = RedisConfig.CACHE_NAME_ACCESS_TOKEN, cacheManager = "redisCacheManager")
class TokenStoreServiceImpl implements  TokenStoreService{
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenStoreServiceImpl(@Qualifier("refreshTokenRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
    public AuthenticatedIamUserInfoDTO saveRefreshToken(String refreshToken, AuthenticatedIamUserInfoDTO idTokenClaims) {
        return idTokenClaims;
    }

    @Override
    public AuthenticatedIamUserInfoDTO saveRefreshToken(String refreshToken, AuthenticatedIamUserInfoDTO idTokenClaims, long ttlInSeconds) {
        String key = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;
        redisTemplate.opsForValue().set(key, idTokenClaims, Duration.ofSeconds(ttlInSeconds));
        return idTokenClaims;
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_NAME_REFRESH_TOKEN, unless="#result == null")
    public AuthenticatedIamUserInfoDTO loadRefreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        String refreshTokenKey = RedisConfig.CACHE_NAME_REFRESH_TOKEN + "::" + refreshToken;
        AuthenticatedIamUserInfoDTO authenticatedIamUserInfoDTO = (AuthenticatedIamUserInfoDTO) redisTemplate.opsForValue().get(refreshTokenKey);
        if (authenticatedIamUserInfoDTO ==null)
            return;
        String accessTokenKey = RedisConfig.CACHE_NAME_ACCESS_TOKEN + "::" + authenticatedIamUserInfoDTO.getAccessToken();
        redisTemplate.delete(List.of(
                refreshTokenKey,
                accessTokenKey
        ));
    }
}
