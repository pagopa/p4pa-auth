package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.configuration.RedisConfig;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = RedisConfig.CACHE_NAME_ACCESS_TOKEN)
class TokenStoreServiceImpl implements  TokenStoreService{
    @Override
    @CachePut
    public UserInfo save(String accessToken, UserInfo idTokenClaims) {
        return idTokenClaims;
    }

    @Override
    @Cacheable
    public UserInfo load(String accessToken) {
        return null;
    }

    @Override
    @CacheEvict
    public void delete(String accessToken) {
        //Do Nothing
    }
}
