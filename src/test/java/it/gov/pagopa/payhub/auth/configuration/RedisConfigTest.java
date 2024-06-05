package it.gov.pagopa.payhub.auth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;

class RedisConfigTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static RedisConfig redisConfig = new RedisConfig();

    @Test
    void testCustomizer(){
        // Given
        int expirationSeconds = 10;

        // When
        RedisCacheManagerBuilderCustomizer result = redisConfig.redisCacheManagerBuilderCustomizer(objectMapper, expirationSeconds);

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
