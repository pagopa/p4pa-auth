package it.gov.pagopa.payhub.auth.config;

import it.gov.pagopa.payhub.auth.utils.Constants;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "it.gov.pagopa.payhub.auth.repository")
public class MongoConfig {

    @Configuration
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    @Setter
    public static class MongoDbCustomProperties {
        ConnectionPoolSettings connectionPool;

        @Setter
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

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                // LocalDateTime support
                new LocalDateTimeWriteConverter(),
                new LocalDateTimeReadConverter()
        ));
    }

    @WritingConverter
    public static class LocalDateTimeWriteConverter implements Converter<LocalDateTime, Date> {
        @Override
        public Date convert(LocalDateTime localDateTime) {
            return Date.from(localDateTime.atZone(Constants.ZONEID).toInstant());
        }
    }

    @ReadingConverter
    public static class LocalDateTimeReadConverter implements Converter<Date, LocalDateTime> {
        @Override
        public LocalDateTime convert(Date date) {
            return date.toInstant().atZone(Constants.ZONEID).toLocalDateTime();
        }
    }
}

