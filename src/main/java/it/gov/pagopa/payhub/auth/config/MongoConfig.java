package it.gov.pagopa.payhub.auth.config;

import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "it.gov.pagopa.payhub.auth.repository")
public class MongoConfig {

    @Configuration
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    @Setter
    public static class MongoDbCustomProperties {
        ConnectionPoolSettings connectionPool;

        static class ConnectionPoolSettings {
            int maxSize;
            int minSize;
            long maxWaitTimeMS;
            long maxConnectionLifeTimeMS;
            long maxConnectionIdleTimeMS;
            int maxConnecting;
        }

    }

    @Bean
    public MongoClientSettingsBuilderCustomizer customizer(MongoDbCustomProperties mongoDbCustomProperties) {
        return builder -> builder.applyToConnectionPoolSettings(
                connectionPool -> {
                    connectionPool.maxSize(mongoDbCustomProperties.connectionPool.maxSize);
                    connectionPool.minSize(mongoDbCustomProperties.connectionPool.minSize);
                    connectionPool.maxWaitTime(mongoDbCustomProperties.connectionPool.maxWaitTimeMS, TimeUnit.MILLISECONDS);
                    connectionPool.maxConnectionLifeTime(mongoDbCustomProperties.connectionPool.maxConnectionLifeTimeMS, TimeUnit.MILLISECONDS);
                    connectionPool.maxConnectionIdleTime(mongoDbCustomProperties.connectionPool.maxConnectionIdleTimeMS, TimeUnit.MILLISECONDS);
                    connectionPool.maxConnecting(mongoDbCustomProperties.connectionPool.maxConnecting);
                });
    }
}

