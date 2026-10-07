package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.dto.UserWithOperator;
import it.gov.pagopa.payhub.auth.model.User;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UsersRepositoryExtImplTest extends BaseMongoRepositoryTest {

    private UsersRepositoryExt repository;

    @BeforeEach
    void init() {
        repository = new UsersRepositoryExtImpl(mongoTemplateMock);
    }

    @AfterEach
    void verifyNotMoreInvocation() {
        Mockito.verifyNoMoreInteractions(mongoTemplateMock);
    }

    @Test
    void whenRegisterUserThenReturnStoredUser() {
        // Given
        User user = User.builder()
                .iamIssuer("IAMISSUER")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .userCode("USERCODE")
                .firstName("FIRSTNAME")
                .lastName("LASTNAME")
                .fiscalCode("FISCALCODE")
                .build();
        User storedUser = new User();

        Mockito.doReturn(storedUser)
                .when(mongoTemplateMock)
                .findAndModify(
                        Mockito.eq(Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(user.getMappedExternalUserId()))),
                        Mockito.argThat(u -> {
                            LocalDateTime lastLogin = u.getUpdateObject().get("$set", Document.class).get(User.Fields.lastLogin, LocalDateTime.class);
                            Assertions.assertNotNull(lastLogin);
                            Assertions.assertFalse(lastLogin.isAfter(LocalDateTime.now()));
                            Assertions.assertTrue(lastLogin.isAfter(LocalDateTime.now().minusMinutes(1)));

                            return argSetTechFieldsOnDocumentUpdateInvoke(new Update()
                                    .set(User.Fields.userCode, user.getUserCode())
                                    .set(User.Fields.iamIssuer, user.getIamIssuer())
                                    .setOnInsert(User.Fields.tosAccepted, false)
                                    .set(User.Fields.lastLogin, lastLogin)
                                    .set(User.Fields.firstName, user.getFirstName())
                                    .set(User.Fields.lastName, user.getLastName())
                                    .set(User.Fields.fiscalCode, user.getFiscalCode())
                            ).matches((Update) u);
                        }),
                        Mockito.argThat(opt -> opt.isReturnNew() && opt.isUpsert() && !opt.isRemove()),
                        Mockito.eq(User.class)
                );

        // When
        User result = repository.registerUser(user);

        // Then
        Assertions.assertSame(storedUser, result);
    }

    @Test
    void whenRetrieveUsersThenOk() {
        // Given
        String fiscalCode = "FISCALCODE";
        String firstName = "FIRSTNAME";
        String lastName = "LASTNAME";
        List<String> mappedExternalUserIdsToExclude = List.of("mappedExternalUserId");

        List<User> users = Collections.singletonList(User.builder().firstName(firstName).lastName(lastName).fiscalCode(fiscalCode).build());
        Pageable pageable = PageRequest.of(0, 10);

        Query expectedQuery = new Query()
                .addCriteria(Criteria.where("fiscalCode").is(fiscalCode))
                .addCriteria(Criteria.where("firstName").is(firstName))
                .addCriteria(Criteria.where("lastName").is(lastName))
                .addCriteria(Criteria.where("mappedExternalUserId").nin(mappedExternalUserIdsToExclude))
                .with(pageable);

        Mockito.when(mongoTemplateMock.count(Mockito.any(Query.class), Mockito.eq(User.class))).thenReturn(1L);
        Mockito.when(mongoTemplateMock.find(Mockito.any(Query.class), Mockito.eq(User.class))).thenReturn(users);

        // When
        Page<User> result = repository.retrieveUsers(fiscalCode, firstName, lastName, mappedExternalUserIdsToExclude, pageable);

        // Verify
        Mockito.verify(mongoTemplateMock).count(expectedQuery, User.class);
        Mockito.verify(mongoTemplateMock).find(expectedQuery, User.class);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(users, result.getContent());
        Assertions.assertEquals(pageable, result.getPageable());
    }

    @Test
    void whenRetrieveUsersWithNoCriteriaThenOk() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String fiscalCode = "FISCALCODE";
        String firstName = "FIRSTNAME";
        String lastName = "LASTNAME";
        List<User> users = Collections.singletonList(User.builder().firstName(firstName).lastName(lastName).fiscalCode(fiscalCode).build());

        Query expectedQuery = new Query().with(pageable);

        Mockito.when(mongoTemplateMock.count(Mockito.any(Query.class), Mockito.eq(User.class))).thenReturn(1L);
        Mockito.when(mongoTemplateMock.find(Mockito.any(Query.class), Mockito.eq(User.class))).thenReturn(users);

        // When
        Page<User> result = repository.retrieveUsers(null, null, null, null,pageable);

        // Verify
        Mockito.verify(mongoTemplateMock).count(expectedQuery, User.class);
        Mockito.verify(mongoTemplateMock).find(expectedQuery, User.class);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(users, result.getContent());
        Assertions.assertEquals(pageable, result.getPageable());
    }

    @Test
    void givenValidCriteriaWhenFindUsersWithOperatorThenReturnPagedData() {
        String organizationIpaCode = "ORG_IPA";
        String fiscalCode = "FISCALCODE";
        String firstName = "FIRSTNAME";
        String lastName = "LASTNAME";
        List<String> mappedExternalUserIdsToExclude = List.of("mappedExternalUserId");
        Pageable pageable = PageRequest.of(0, 10);

        Document countDoc = new Document("totalElements", 1);
        AggregationResults<Document> countResults = new AggregationResults<>(List.of(countDoc), new Document());
        Mockito.when(mongoTemplateMock.aggregate(Mockito.any(Aggregation.class), Mockito.eq(User.class), Mockito.eq(Document.class)))
                .thenReturn(countResults);

        UserWithOperator expectedUser = new UserWithOperator();
        AggregationResults<UserWithOperator> dataResults = new AggregationResults<>(List.of(expectedUser), new Document());
        Mockito.when(mongoTemplateMock.aggregate(Mockito.any(Aggregation.class), Mockito.eq(User.class), Mockito.eq(UserWithOperator.class)))
                .thenReturn(dataResults);

        Page<UserWithOperator> result = repository.findUsersWithOperator(
                organizationIpaCode, fiscalCode, firstName, lastName, mappedExternalUserIdsToExclude, pageable
        );

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertSame(expectedUser, result.getContent().getFirst());
    }

    @Test
    void givenZeroMatchingUsersWhenFindUsersWithOperatorThenReturnEmptyPage() {
        String organizationIpaCode = "ORG_IPA";
        Pageable pageable = PageRequest.of(0, 10);

        AggregationResults<Document> countResults = new AggregationResults<>(Collections.emptyList(), new Document());
        Mockito.when(mongoTemplateMock.aggregate(Mockito.any(Aggregation.class), Mockito.eq(User.class), Mockito.eq(Document.class)))
                .thenReturn(countResults);

        Page<UserWithOperator> result = repository.findUsersWithOperator(
                organizationIpaCode, "FISCALCODE", "FIRSTNAME", "LASTNAME", null, pageable
        );

        Assertions.assertEquals(0, result.getTotalElements());
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    @Test
    void givenNoOptionalCriteriaAndSortedPageableWhenFindUsersWithOperatorThenReturnPagedData() {
        String organizationIpaCode = "ORG_IPA";
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "fiscalCode"));

        Document countDoc = new Document("totalElements", 12);
        AggregationResults<Document> countResults = new AggregationResults<>(List.of(countDoc), new Document());
        Mockito.when(mongoTemplateMock.aggregate(Mockito.any(Aggregation.class), Mockito.eq(User.class), Mockito.eq(Document.class)))
                .thenReturn(countResults);

        UserWithOperator expectedUser1 = new UserWithOperator();
        UserWithOperator expectedUser2 = new UserWithOperator();
        AggregationResults<UserWithOperator> dataResults = new AggregationResults<>(List.of(expectedUser1, expectedUser2), new Document());
        Mockito.when(mongoTemplateMock.aggregate(Mockito.any(Aggregation.class), Mockito.eq(User.class), Mockito.eq(UserWithOperator.class)))
                .thenReturn(dataResults);

        Page<UserWithOperator> result = repository.findUsersWithOperator(
                organizationIpaCode, null, null, null, Collections.emptyList(), pageable
        );

        Assertions.assertEquals(12, result.getTotalElements());
        Assertions.assertEquals(2, result.getContent().size());
    }
}
