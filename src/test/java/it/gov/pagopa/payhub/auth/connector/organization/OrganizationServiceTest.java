package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationSearchClient organizationSearchClientMock;

    private OrganizationService service;

    @BeforeEach
    void init(){
        service = new OrganizationServiceImpl(organizationSearchClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationSearchClientMock
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

}
