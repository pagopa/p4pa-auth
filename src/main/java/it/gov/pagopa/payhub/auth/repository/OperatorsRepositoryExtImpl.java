package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Set;

public class OperatorsRepositoryExtImpl implements OperatorsRepositoryExt{
    private final MongoTemplate mongoTemplate;

    public OperatorsRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles) {
        return mongoTemplate.findAndModify(
                Query.query(Criteria
                        .where(Operator.Fields.userId).is(userId)
                        .and(Operator.Fields.organizationIpaCode).is(organizationIpaCode)),
                new Update()
                        .set(Operator.Fields.roles, roles),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                Operator.class
        );
    }
}
