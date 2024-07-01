package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class UsersRepositoryExtImplTest {

    @Mock
    private MongoTemplate mongoTemplateMock;

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
                .email("EMAIL")
                .build();
        User storedUser = new User();

        Mockito.when(mongoTemplateMock.findAndModify(
                Mockito.eq(Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(user.getMappedExternalUserId()))),
                Mockito.argThat(u -> {
                    LocalDateTime lastLogin = u.getUpdateObject().get("$set", Document.class).get(User.Fields.lastLogin, LocalDateTime.class);
                    Assertions.assertNotNull(lastLogin);
                    Assertions.assertFalse(lastLogin.isAfter(LocalDateTime.now()));
                    Assertions.assertTrue(lastLogin.isAfter(LocalDateTime.now().minusMinutes(1)));

                    return u.equals(new Update()
                            .set(User.Fields.userCode, user.getUserCode())
                            .set(User.Fields.iamIssuer, user.getIamIssuer())
                            .setOnInsert(User.Fields.tosAccepted, false)
                            .set(User.Fields.lastLogin, lastLogin)
                            .set(User.Fields.firstName, user.getFirstName())
                            .set(User.Fields.lastName, user.getLastName())
                            .set(User.Fields.email, user.getEmail())
                            .set(User.Fields.fiscalCode, user.getFiscalCode())
                    );
                }),
                Mockito.argThat(opt -> opt.isReturnNew() && opt.isUpsert() && !opt.isRemove()),
                Mockito.eq(User.class)
        )).thenReturn(storedUser);

        // When
        User result = repository.registerUser(user);

        // Then
        Assertions.assertSame(storedUser, result);
    }
}
