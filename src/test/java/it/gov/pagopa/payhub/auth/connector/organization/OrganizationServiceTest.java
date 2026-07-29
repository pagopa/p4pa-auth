package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrganizationClient;
import it.gov.pagopa.payhub.auth.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationSearchClient organizationSearchClientMock;
    @Mock
    private OrganizationClient organizationClientMock;
    @InjectMocks
    private OrganizationServiceImpl service;


    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationSearchClientMock,
                organizationClientMock
        );
    }

    @Test
    void whenGetOrganizationByIpaCodeByIdThenInvokeClient(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String orgIpaCode = "ORGIPACODE";
        Organization expectedResult = new Organization();

        Mockito.when(organizationSearchClientMock.getOrganizationByIpaCode(orgIpaCode, accessToken))
                .thenReturn(expectedResult);

        // When
        Organization result = service.getOrganizationByIpaCode(orgIpaCode, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenUpdateOrganizationExternalIdThenInvokeClient(){
        // Given
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        String organizationExternalId = "ORG_EXT_ID";

        doNothing().when(organizationClientMock).updateOrganizationExternalId(organizationId, organizationExternalId, accessToken);
        // When Then
        Assertions.assertDoesNotThrow(() -> service.updateOrganizationExternalId(organizationId, organizationExternalId, accessToken));
    }

}
