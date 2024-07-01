package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;

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
                        .set(User.Fields.userCode, user.getUserCode())
                        .set(User.Fields.iamIssuer, user.getIamIssuer())
                        .setOnInsert(User.Fields.tosAccepted, false)
                        .set(User.Fields.lastLogin, LocalDateTime.now())

                        .set(User.Fields.fiscalCode, user.getFiscalCode())
                        .set(User.Fields.firstName, user.getFirstName())
                        .set(User.Fields.lastName, user.getLastName())
                        .set(User.Fields.email, user.getEmail()),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                User.class
        );
    }
}
