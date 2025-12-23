package it.gov.pagopa.payhub.auth.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;
import tools.jackson.databind.json.JsonMapper;

class RedisConfigTest {

    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final RedisConfig redisConfig = new RedisConfig();

    @Test
    void testCustomizer(){
        // Given
        int expirationSeconds = 10;

        // When
        RedisCacheManagerBuilderCustomizer result = redisConfig.redisCacheManagerBuilderCustomizer(jsonMapper, expirationSeconds);

        // Then
        Assertions.assertNotNull(result);

        // When
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilderMock = Mockito.mock(RedisCacheManager.RedisCacheManagerBuilder.class);
        result.customize(redisCacheManagerBuilderMock);

        // Then
        Mockito.verify(redisCacheManagerBuilderMock).withCacheConfiguration(
                Mockito.eq(RedisConfig.CACHE_NAME_ACCESS_TOKEN),
                Mockito.argThat(i ->
                        !i.getAllowCacheNullValues() &&
                                ("FixedDurationTtlFunction[duration=PT"+expirationSeconds+"S]").equals(i.getTtlFunction().toString()))
        );

        Mockito.verifyNoMoreInteractions(redisCacheManagerBuilderMock);
    }

}
