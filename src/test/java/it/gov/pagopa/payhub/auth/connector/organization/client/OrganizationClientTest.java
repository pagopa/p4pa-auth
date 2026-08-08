package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
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

    @Test
    void givenNoExistentOrganizationWhenUpdateExternalOrganizationIdThenResourceNotFoundException() {
        Long organizationId = 99L;
        String accessToken = "ACCESSTOKEN";
        String organizationExternalId = "ORG_EXT_ID";

        when(organizationApisHolderMock.getOrganizationApi(accessToken))
                .thenReturn(organizationApiMock);
        doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null))
                .when(organizationApiMock).updateOrganizationExternalId(organizationId, organizationExternalId);

        Assertions.assertThrows(ResourceNotFoundException.class,() ->
                organizationClient.updateExternalOrganizationId(organizationId, organizationExternalId, accessToken));
    }

}