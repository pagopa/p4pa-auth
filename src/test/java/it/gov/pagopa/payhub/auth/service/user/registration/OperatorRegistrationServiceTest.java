package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class OperatorRegistrationServiceTest {

    @Mock
    private OperatorsRepository operatorsRepositoryMock;

    private OperatorRegistrationService service;

    @BeforeEach
    void init() {
        service = new OperatorRegistrationService(operatorsRepositoryMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(operatorsRepositoryMock);
    }

    @Test
    void whenRegisterOperatorThenReturnStoredOperator() {
        // Given
        String userId = "USERID";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();

        Mockito.when(operatorsRepositoryMock.registerOperator(userId, organizationIpaCode, roles))
                .thenReturn(storedOperator);

        // When
        Operator result = service.registerOperator(userId, organizationIpaCode, roles);

        // Then
        Assertions.assertSame(storedOperator, result);
    }
}
