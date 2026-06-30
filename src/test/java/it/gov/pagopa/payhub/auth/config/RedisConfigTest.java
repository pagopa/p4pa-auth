package it.gov.pagopa.payhub.auth.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

class RedisConfigTest {

    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final RedisConfig redisConfig = new RedisConfig();

    @Test
    void testCustomizer(){
        // Given
        int expirationSeconds = 10;
        int refreshExpirationSeconds = 1000;

        String expectedAccessTtl = Duration.ofSeconds(expirationSeconds).toString();
        String expectedRefreshTtl = Duration.ofSeconds(refreshExpirationSeconds).toString();

        // When
        RedisCacheManagerBuilderCustomizer result = redisConfig.redisCacheManagerBuilderCustomizer(jsonMapper, expirationSeconds, refreshExpirationSeconds);

        // Then
        Assertions.assertNotNull(result);

        // When
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilderMock = Mockito.mock(RedisCacheManager.RedisCacheManagerBuilder.class);
        Mockito.when(redisCacheManagerBuilderMock.withCacheConfiguration(Mockito.anyString(), Mockito.any()))
                .thenReturn(redisCacheManagerBuilderMock);
        result.customize(redisCacheManagerBuilderMock);

        // Then
        Mockito.verify(redisCacheManagerBuilderMock).withCacheConfiguration(
                Mockito.eq(RedisConfig.CACHE_NAME_ACCESS_TOKEN),
                Mockito.argThat(i ->
                        !i.getAllowCacheNullValues() &&
                                ("FixedDurationTtlFunction[duration=" + expectedAccessTtl + "]").equals(i.getTtlFunction().toString()))
        );

        Mockito.verify(redisCacheManagerBuilderMock).withCacheConfiguration(
                Mockito.eq(RedisConfig.CACHE_NAME_REFRESH_TOKEN),
                Mockito.argThat(i ->
                        !i.getAllowCacheNullValues() &&
                                ("FixedDurationTtlFunction[duration=" + expectedRefreshTtl + "]").equals(i.getTtlFunction().toString()))
        );

        Mockito.verifyNoMoreInteractions(redisCacheManagerBuilderMock);
    }

}
