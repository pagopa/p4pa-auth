package it.gov.pagopa.payhub.auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String CACHE_NAME_ACCESS_TOKEN = "ACCESS_TOKEN";

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            @Value("${jwt.access-token.expire-in}") int accessTokenExpirationSeconds
    ) {
        return builder -> builder
                .withCacheConfiguration(CACHE_NAME_ACCESS_TOKEN,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(accessTokenExpirationSeconds))
                                .disableCachingNullValues()
                );
    }

}
