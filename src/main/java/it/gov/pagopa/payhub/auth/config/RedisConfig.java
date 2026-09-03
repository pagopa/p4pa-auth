package it.gov.pagopa.payhub.auth.config;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String CACHE_NAME_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String CACHE_NAME_REFRESH_TOKEN = "REFRESH_TOKEN";

    @Bean
    public RedisCacheManager redisCacheManager(
            ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
            RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory);
        builder.enableStatistics();
        redisCacheManagerBuilderCustomizers.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            JsonMapper jsonMapper,
            @Value("${jwt.access-token.expire-in}") int accessTokenExpirationSeconds,
            @Value("${jwt.refresh-token.expire-in}") int refreshTokenExpirationSeconds
    ) {
        return builder -> builder
                .withCacheConfiguration(CACHE_NAME_ACCESS_TOKEN,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(jsonMapper, IamUserInfoDTO.class)))
                                .entryTtl(Duration.ofSeconds(accessTokenExpirationSeconds))
                                .disableCachingNullValues()
                )
                .withCacheConfiguration(CACHE_NAME_REFRESH_TOKEN,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(jsonMapper, IamUserInfoDTO.class)))
                                .entryTtl(Duration.ofSeconds(refreshTokenExpirationSeconds))
                                .disableCachingNullValues()
                );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            JsonMapper jsonMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        JacksonJsonRedisSerializer<IamUserInfoDTO> serializer =
                new JacksonJsonRedisSerializer<>(jsonMapper, IamUserInfoDTO.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

}
