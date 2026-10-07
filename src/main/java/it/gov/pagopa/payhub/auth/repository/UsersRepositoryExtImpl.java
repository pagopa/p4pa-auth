package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.config.BaseEntityListener;
import it.gov.pagopa.payhub.auth.dto.UserWithOperator;
import it.gov.pagopa.payhub.auth.model.User;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersRepositoryExtImpl implements UsersRepositoryExt{

    private final MongoTemplate mongoTemplate;

    public UsersRepositoryExtImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User registerUser(User user) {
        return mongoTemplate.findAndModify(
                Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(user.getMappedExternalUserId())),
                BaseEntityListener.setTechFieldsOnDocumentUpdate(new Update()
                        .set(User.Fields.userCode, user.getUserCode())
                        .set(User.Fields.iamIssuer, user.getIamIssuer())
                        .setOnInsert(User.Fields.tosAccepted, false)
                        .set(User.Fields.lastLogin, LocalDateTime.now())

                        .set(User.Fields.fiscalCode, user.getFiscalCode())
                        .set(User.Fields.firstName, user.getFirstName())
                        .set(User.Fields.lastName, user.getLastName())
                ),
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                User.class
        );
    }

    @Override
    public Page<User> retrieveUsers(String fiscalCode, String firstName, String lastName, List<String> mappedExternalUserIdsToExclude,
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
        if (mappedExternalUserIdsToExclude != null && !mappedExternalUserIdsToExclude.isEmpty()) {
            query.addCriteria(Criteria.where("mappedExternalUserId").nin(mappedExternalUserIdsToExclude));
        }
        long count = mongoTemplate.count(query, User.class);
        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);
        return new PageImpl<>(users, pageable, count);
    }

    @Override
    public Page<UserWithOperator> findUsersWithOperator(
            String organizationIpaCode,
            String fiscalCode,
            String firstName,
            String lastName,
            List<String> mappedExternalUserIdsToExclude,
            Pageable pageable
    ) {
        List<AggregationOperation> baseOperations = new ArrayList<>();

        buildUserMatchOperation(fiscalCode, firstName, lastName, mappedExternalUserIdsToExclude)
                .ifPresent(baseOperations::add);

        baseOperations.addAll(buildLookupAndOperatorMatchOperations(organizationIpaCode));

        long total = countTotalElements(baseOperations);
        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<UserWithOperator> results = fetchPagedData(baseOperations, pageable);

        return new PageImpl<>(results, pageable, total);
    }

    private Optional<AggregationOperation> buildUserMatchOperation(
            String fiscalCode, String firstName, String lastName, List<String> mappedExternalUserIdsToExclude
    ) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (fiscalCode != null && !fiscalCode.isEmpty()) {
            criteriaList.add(Criteria.where("fiscalCode").is(fiscalCode));
        }
        if (lastName != null && !lastName.isEmpty()) {
            criteriaList.add(Criteria.where("lastName").is(lastName));
        }
        if (firstName != null && !firstName.isEmpty()) {
            criteriaList.add(Criteria.where("firstName").is(firstName));
        }
        if (mappedExternalUserIdsToExclude != null && !mappedExternalUserIdsToExclude.isEmpty()) {
            criteriaList.add(Criteria.where("mappedExternalUserId").nin(mappedExternalUserIdsToExclude));
        }

        if (!criteriaList.isEmpty()) {
            return Optional.of(Aggregation.match(
                    new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))
            ));
        }

        return Optional.empty();
    }

    private List<AggregationOperation> buildLookupAndOperatorMatchOperations(String organizationIpaCode) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.addFields()
                .addField("userIdAsString")
                .withValueOf(ConvertOperators.ToString.toString("$_id"))
                .build());

        operations.add(Aggregation.lookup(
                "operators",
                "userIdAsString",
                "userId",
                "operator"
        ));

        operations.add(Aggregation.unwind("operator", false));

        operations.add(Aggregation.match(Criteria.where("operator.organizationIpaCode").is(organizationIpaCode)));

        return operations;
    }

    private long countTotalElements(List<AggregationOperation> baseOperations) {
        List<AggregationOperation> countOps = new ArrayList<>(baseOperations);
        countOps.add(Aggregation.count().as("totalElements"));

        return mongoTemplate.aggregate(Aggregation.newAggregation(countOps), User.class, Document.class)
                .getMappedResults().stream().findFirst()
                .map(doc -> doc.getInteger("totalElements").longValue())
                .orElse(0L);
    }

    private List<UserWithOperator> fetchPagedData(List<AggregationOperation> baseOperations, Pageable pageable) {
        List<AggregationOperation> dataOps = new ArrayList<>(baseOperations);

        if (pageable.getSort().isSorted()) {
            dataOps.add(Aggregation.sort(pageable.getSort()));
        }

        dataOps.add(Aggregation.skip(pageable.getOffset()));
        dataOps.add(Aggregation.limit(pageable.getPageSize()));

        return mongoTemplate.aggregate(
                Aggregation.newAggregation(dataOps),
                User.class,
                UserWithOperator.class
        ).getMappedResults();
    }
}
