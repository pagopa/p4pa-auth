package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.payhub.auth.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationSearchClientTest {
    @Mock
    private OrganizationApisHolder organizationApisHolder;
    @Mock
    private OrganizationSearchControllerApi organizationSearchControllerApiMock;

    private OrganizationSearchClient organizationSearchClient;

    @BeforeEach
    void setUp() {
        organizationSearchClient = new OrganizationSearchClient(organizationApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationApisHolder
        );
    }

    @Test
    void whenGetOrganizationByIpaCodeThenInvokeWithAccessToken() {
        String orgIpaCode = "ORGIPACODE";
        String accessToken = "ACCESSTOKEN";
        Organization expectedResult = new Organization();

        when(organizationApisHolder.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenReturn(expectedResult);

        Organization result = organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNoExistentIpaCodeWhenGetOrganizationByIpaCodeThenNull() {
        String orgIpaCode = "ORGIPACODE";
        String accessToken = "ACCESSTOKEN";

        when(organizationApisHolder.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "NOTFOUND", "NOTFOUND", "Not found"));

        Organization result = organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, accessToken);

        Assertions.assertNull(result);
    }

    @Test
    void givenGenericHttpExceptionWhenGetOrganizationByIpaCodeThenThrowIt() {
        String orgIpaCode = "ORGIPACODE";
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException expectedException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(organizationApisHolder.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenThrow(expectedException);

        HttpClientErrorException result = Assertions.assertThrows(expectedException.getClass(), () -> organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, accessToken));

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void givenGenericExceptionWhenGetOrganizationByIpaCodeThenThrowIt() {
        String orgIpaCode = "ORGIPACODE";
        String accessToken = "ACCESSTOKEN";
        RuntimeException expectedException = new RuntimeException();

        when(organizationApisHolder.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenThrow(expectedException);

        RuntimeException result = Assertions.assertThrows(expectedException.getClass(), () -> organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, accessToken));

        Assertions.assertSame(expectedException, result);
    }

}
