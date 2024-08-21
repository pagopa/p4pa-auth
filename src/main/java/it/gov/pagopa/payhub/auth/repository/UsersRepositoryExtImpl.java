package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<User> retrieveUsers(String fiscalCode, String firstName, String lastName,
        Pageable pageable) {
        Query query = new Query();
        if (fiscalCode != null && !fiscalCode.isEmpty()) {
            query.addCriteria(Criteria.where("fiscalCode").is(fiscalCode));
        }
        if (lastName != null && !lastName.isEmpty()) {
            query.addCriteria(Criteria.where("lastName").is(lastName));
        }
        if (firstName != null && !firstName.isEmpty()) {
            query.addCriteria(Criteria.where("firstName").is(firstName));
        }
        long count = mongoTemplate.count(query, User.class);
        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);
        return new PageImpl<>(users, pageable, count);
    }
}
