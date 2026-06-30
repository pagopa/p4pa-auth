package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.config.RedisConfig;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = RedisConfig.CACHE_NAME_ACCESS_TOKEN, cacheManager = "redisCacheManager")
class TokenStoreServiceImpl implements  TokenStoreService{
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
    public String saveRefreshToken(String refreshToken, String externalUserId) {
        return externalUserId;
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CACHE_NAME_REFRESH_TOKEN, unless="#result == null")
    public String loadRefreshToken(String refreshToken) {
        return null;
    }

    @Override
    @CacheEvict(cacheNames = RedisConfig.CACHE_NAME_REFRESH_TOKEN)
    public void deleteRefreshToken(String refreshToken) {
        // Do Nothing
    }
}
