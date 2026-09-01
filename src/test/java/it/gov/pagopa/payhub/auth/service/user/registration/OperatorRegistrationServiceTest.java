package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.connector.debtposition.DebtPositionTypeOrgOperatorService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorRegistrationServiceTest {

    @Mock
    private OperatorsRepository operatorsRepositoryMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private DebtPositionTypeOrgOperatorService debtPositionTypeOrgOperatorServiceMock;
    @InjectMocks
    private OperatorRegistrationService service;

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(operatorsRepositoryMock);
    }

    @Test
    void givenOrganizationFoundWhenRegisterOperatorThenReturnStoredOperator() {
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

        when(operatorsRepositoryMock.registerOperator(user.getUserId(), organizationIpaCode, email, roles))
                .thenReturn(storedOperator);
        when(organizationServiceMock.getOrganizationByIpaCode(organizationIpaCode, accessToken))
                .thenReturn(organization);
        doNothing()
                .when(debtPositionTypeOrgOperatorServiceMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        user.getMappedExternalUserId(),
                        organization.getOrganizationId(),
                        accessToken
                );
        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, null, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
    }

    @Test
    void givenOrganizationNotFoundWhenRegisterOperatorThenReturnStoredOperator() {
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

        when(operatorsRepositoryMock.registerOperator(user.getUserId(), organizationIpaCode, email, roles))
                .thenReturn(storedOperator);
        when(organizationServiceMock.getOrganizationByIpaCode(organizationIpaCode, accessToken))
                .thenReturn(null);

        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, null, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
        verify(debtPositionTypeOrgOperatorServiceMock, times(0))
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        Mockito.anyString(),
                        Mockito.anyLong(),
                        Mockito.anyString()
                );
    }

    @Test
    void givenSameExternalIdWhenRegisterOperatorThenDoNotUpdateExternalId() {
        // Given
        String accessToken = "ACCESS_TOKEN";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        String email = "EMAIL";
        String sameExternalId = "SAME_EXT_ID";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();
        Organization organization = new Organization();
        organization.setOrganizationId(1L);
        organization.setExternalOrganizationId(sameExternalId); // Stesso ID gia a DB

        User user = new User();
        user.setUserId("USERID");
        user.setMappedExternalUserId("operatorExternalUserId");

        when(operatorsRepositoryMock.registerOperator(user.getUserId(), organizationIpaCode, email, roles))
                .thenReturn(storedOperator);
        when(organizationServiceMock.getOrganizationByIpaCode(organizationIpaCode, accessToken))
                .thenReturn(organization);
        doNothing()
                .when(debtPositionTypeOrgOperatorServiceMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        user.getMappedExternalUserId(),
                        organization.getOrganizationId(),
                        accessToken
                );

        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, sameExternalId, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
        verify(organizationServiceMock, never())
                .updateExternalOrganizationId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void givenDifferentExternalIdWhenRegisterOperatorThenUpdateExternalId() {
        // Given
        String accessToken = "ACCESS_TOKEN";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        String email = "EMAIL";
        String oldExternalId = "OLD_EXT_ID";
        String newExternalId = "NEW_EXT_ID";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();
        Organization organization = new Organization();
        organization.setOrganizationId(1L);
        organization.setExternalOrganizationId(oldExternalId);

        User user = new User();
        user.setUserId("USERID");
        user.setMappedExternalUserId("operatorExternalUserId");

        when(operatorsRepositoryMock.registerOperator(user.getUserId(), organizationIpaCode, email, roles))
                .thenReturn(storedOperator);
        when(organizationServiceMock.getOrganizationByIpaCode(organizationIpaCode, accessToken))
                .thenReturn(organization);
        doNothing()
                .when(debtPositionTypeOrgOperatorServiceMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        user.getMappedExternalUserId(),
                        organization.getOrganizationId(),
                        accessToken
                );
        doNothing()
                .when(organizationServiceMock)
                .updateExternalOrganizationId(1L, newExternalId, organizationIpaCode, accessToken);

        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, newExternalId, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
        verify(organizationServiceMock, times(1))
                .updateExternalOrganizationId(1L, newExternalId, organizationIpaCode, accessToken);
    }
}
