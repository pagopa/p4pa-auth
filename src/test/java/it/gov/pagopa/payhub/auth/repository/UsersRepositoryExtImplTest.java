package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
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

@ExtendWith(MockitoExtension.class)
class UsersRepositoryExtImplTest {

    @Mock
    private MongoTemplate mongoTemplateMock;

    private UsersRepositoryExt repository;

    @BeforeEach
    void init(){
        repository = new UsersRepositoryExtImpl(mongoTemplateMock);
    }

    @AfterEach
    void verifyNotMoreInvocation(){
        Mockito.verifyNoMoreInteractions(mongoTemplateMock);
    }

    @Test
    void whenRegisterUserThenReturnStoredUser(){
        // Given
        User user = User.builder()
                .iamIssuer("IAMISSUER")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .userCode("USERCODE")
                .build();
        User storedUser = new User();

        Mockito.when(mongoTemplateMock.findAndModify(
                Mockito.eq(Query.query(Criteria.where(User.Fields.mappedExternalUserId).is(user.getMappedExternalUserId()))),
                Mockito.eq(new Update()
                        .setOnInsert(User.Fields.userCode, user.getUserCode())
                        .setOnInsert(User.Fields.iamIssuer, user.getIamIssuer())
                        .setOnInsert(User.Fields.tosAccepted, false)
                        .currentTimestamp(User.Fields.lastLogin)),
                Mockito.argThat(opt -> opt.isReturnNew() && opt.isUpsert() && !opt.isRemove()),
                Mockito.eq(User.class)
        )).thenReturn(storedUser);

        // When
        User result = repository.registerUser(user);

        // Then
        Assertions.assertSame(storedUser, result);
    }
}
