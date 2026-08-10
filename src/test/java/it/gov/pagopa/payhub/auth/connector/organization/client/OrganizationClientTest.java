package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationApi organizationApiMock;
    @Mock
    private HttpClientErrorException.BadRequest badRequestMock;
    @InjectMocks
    private OrganizationClient organizationClient;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock,
                organizationApiMock,
                badRequestMock
        );
    }

    @Test
    void givenExistingOrganizationWhenUpdateExternalOrganizationIdThenInvokeWithAccessToken() {
        String accessToken = "ACCESSTOKEN";
        String organizationExternalId = "ORG_EXT_ID";
        Long organizationId = 1L;

        when(organizationApisHolderMock.getOrganizationApi(accessToken))
                .thenReturn(organizationApiMock);
        Mockito.doNothing().when(organizationApiMock).updateOrganizationExternalId(organizationId, organizationExternalId);

        assertDoesNotThrow(() -> organizationClient.updateExternalOrganizationId(organizationId, organizationExternalId, accessToken));
    }

}