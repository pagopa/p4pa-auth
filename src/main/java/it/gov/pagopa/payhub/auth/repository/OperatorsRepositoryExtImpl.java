package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
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
    public Operator registerOperator(String userId, String organizationIpaCode, String email, Set<String> roles) {
        return mongoTemplate.findAndModify(
                Query.query(Criteria
                        .where(Operator.Fields.operatorId).is(userId+organizationIpaCode)),
                new Update()
                        .set(Operator.Fields.userId, userId)
                        .set(Operator.Fields.organizationIpaCode, organizationIpaCode)
                        .set(Operator.Fields.email, email)
                        .set(Operator.Fields.roles, roles),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                Operator.class
        );
    }

    @Override
    public void deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId) {
        //find User with mappedExternalUserId
        User user = mongoTemplate.findOne(Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(mappedExternalUserId)),
            User.class);
        //If exists delete Operator
        if(user!=null)
            mongoTemplate.remove(
                Query.query(Criteria
                    .where(Operator.Fields.organizationIpaCode).is(organizationIpaCode)
                    .and(Operator.Fields.userId).is(user.getUserId())),Operator.class);
    }
}
