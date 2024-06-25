package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UsersRepositoryExtImpl implements UsersRepositoryExt{

    private final MongoTemplate mongoTemplate;

    public UsersRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User registerUser(User user) {
        return mongoTemplate.findAndModify(
                Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(user.getMappedExternalUserId())),
                new Update()
                        .setOnInsert(User.Fields.userCode, user.getUserCode())
                        .setOnInsert(User.Fields.iamIssuer, user.getIamIssuer())
                        .setOnInsert(User.Fields.tosAccepted, false)
                        .currentTimestamp(User.Fields.lastLogin),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                User.class
        );
    }
}
