package it.gov.pagopa.payhub.auth.config;

import org.bson.Document;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/** The actual version of cosmosDb-MongoDB doesn't support hello command,introduced by Spring-boot 3.3.4, rollback to previous command */
@Component
public class MongoHealthIndicator extends AbstractHealthIndicator {
    private final MongoTemplate mongoTemplate;

    public MongoHealthIndicator(MongoTemplate mongoTemplate) {
        super("MongoDB health check failed");
        Assert.notNull(mongoTemplate, "MongoTemplate must not be null");
        this.mongoTemplate = mongoTemplate;
    }

    protected void doHealthCheck(Health.Builder builder) {
        Document result = this.mongoTemplate.executeCommand("{ isMaster: 1 }");
        builder.up().withDetail("maxWireVersion", result.getInteger("maxWireVersion"));
    }
}
