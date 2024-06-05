package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.configuration.RedisConfig;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@CacheConfig(cacheNames = RedisConfig.CACHE_NAME_ACCESS_TOKEN)
class TokenStoreServiceImpl implements  TokenStoreService{
    @Override
    @CachePut
    public Map<String, String> save(String accessToken, Map<String, String> idTokenClaims) {
        return idTokenClaims;
    }

    @Override
    @Cacheable
    public Map<String, String> load(String accessToken) {
        return null;
    }

    @Override
    @CacheEvict
    public void delete(String accessToken) {
        //Do Nothing
    }
}
