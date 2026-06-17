package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.connector.debtposition.DebtPositionTypeOrgOperatorService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
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
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private DebtPositionTypeOrgOperatorService debtPositionTypeOrgOperatorServiceMock;

    private OperatorRegistrationService service;

    @BeforeEach
    void init() {
        service = new OperatorRegistrationService(operatorsRepositoryMock, organizationServiceMock, debtPositionTypeOrgOperatorServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(operatorsRepositoryMock);
    }

    @Test
    void whenRegisterOperatorThenReturnStoredOperator() {
        // Given
        String accessToken = "ACCESS_TOKEN";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        String email = "EMAIL";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();
        Organization organization = new Organization();
        organization.setOrganizationId(1L);
        User user = new User();
        user.setUserId("USERID");
        user.setMappedExternalUserId("operatorExternalUserId");

        Mockito.when(operatorsRepositoryMock.registerOperator(user.getUserId(), organizationIpaCode, email, roles))
                .thenReturn(storedOperator);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(organizationIpaCode, accessToken))
                .thenReturn(organization);
        Mockito.doNothing()
                .when(debtPositionTypeOrgOperatorServiceMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        user.getMappedExternalUserId(),
                        organization.getOrganizationId(),
                        accessToken
                );
        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
    }
}
