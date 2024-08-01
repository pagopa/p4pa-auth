package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
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

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class OperatorsRepositoryExtImplTest {

    @Mock
    private MongoTemplate mongoTemplateMock;

    private OperatorsRepositoryExt repository;

    @BeforeEach
    void init() {
        repository = new OperatorsRepositoryExtImpl(mongoTemplateMock);
    }

    @AfterEach
    void verifyNotMoreInvocation() {
        Mockito.verifyNoMoreInteractions(mongoTemplateMock);
    }

    @Test
    void whenRegisterUserThenReturnStoredUser() {
        // Given
        String userId="USERID";
        String organizationIpaCode="ORGANIZATIONIPACODE";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();

        Mockito.when(mongoTemplateMock.findAndModify(
                Mockito.eq(Query.query(Criteria
                        .where(Operator.Fields.operatorId).is(userId+organizationIpaCode))),
                Mockito.eq(new Update()
                        .set(Operator.Fields.operatorId, userId+organizationIpaCode)
                        .set(Operator.Fields.roles, roles)),
                Mockito.argThat(opt -> opt.isReturnNew() && opt.isUpsert() && !opt.isRemove()),
                Mockito.eq(Operator.class)
        )).thenReturn(storedOperator);

        // When
        Operator result = repository.registerOperator(userId, organizationIpaCode, roles);

        // Then
        Assertions.assertSame(storedOperator, result);
    }
}
