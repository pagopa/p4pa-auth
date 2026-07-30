package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.OrgSubUnitSearchControllerApi;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.CollectionModelOrgSubUnit;
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

@ExtendWith(MockitoExtension.class)
class OrgSubUnitSearchClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolder;

    @Mock
    private OrgSubUnitSearchControllerApi orgSubUnitSearchControllerApiMock;

    private OrgSubUnitSearchClient orgSubUnitSearchClient;

    @BeforeEach
    void setUp() {
        orgSubUnitSearchClient = new OrgSubUnitSearchClient(organizationApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(organizationApisHolder);
    }

    @Test
    void whenGetAllOrgSubUnitsByOrganizationIdThenInvokeWithAccessToken() {
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        CollectionModelOrgSubUnit expectedResult = new CollectionModelOrgSubUnit();

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(organizationId))
                .thenReturn(expectedResult);

        CollectionModelOrgSubUnit result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(organizationId, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNoExistingOrganizationIdWhenGetAllOrgSubUnitsThenNull() {
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(organizationId))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "NotFound",
                        null,
                        null,
                        null
                )
        );

        CollectionModelOrgSubUnit result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(organizationId, accessToken);

        Assertions.assertNull(result);
    }

    @Test
    void givenGenericHttpExceptionWhenGetAllOrgSubUnitsByOrganizationIdThenThrowIt() {
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException expectedException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(organizationId))
                .thenThrow(expectedException);

        HttpClientErrorException result = Assertions.assertThrows(
                expectedException.getClass(),
                () -> orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationId(
                                organizationId,
                                accessToken
                        )
        );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void givenGenericExceptionWhenGetAllOrgSubUnitsByOrganizationIdThenThrowIt() {
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        RuntimeException expectedException = new RuntimeException();

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(organizationId))
                .thenThrow(expectedException);

        RuntimeException result = Assertions.assertThrows(
                expectedException.getClass(),
                () -> orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationId(
                                organizationId,
                                accessToken
                        )
        );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void whenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenInvokeWithAccessToken() {
        Long organizationId = 1L;
        String operatorExternalUserId = "OPERATOREXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";
        CollectionModelOrgSubUnit expectedResult = new CollectionModelOrgSubUnit();

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock
                        .crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId))
                .thenReturn(expectedResult);

        CollectionModelOrgSubUnit result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId,
                                accessToken
                        );

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNoExistingOrganizationIdAndOperatorWhenGetAllOrgSubUnitsThenNull() {
        Long organizationId = 1L;
        String operatorExternalUserId = "OPERATOREXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "NotFound",
                        null,
                        null,
                        null
                )
        );

        CollectionModelOrgSubUnit result =
                orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId,
                                accessToken
                        );

        Assertions.assertNull(result);
    }

    @Test
    void givenGenericHttpExceptionWhenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenThrowIt() {
        Long organizationId = 1L;
        String operatorExternalUserId = "OPERATOREXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException expectedException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(
                orgSubUnitSearchControllerApiMock
                        .crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId
                        )
        ).thenThrow(expectedException);

        HttpClientErrorException result = Assertions.assertThrows(
                expectedException.getClass(),
                () -> orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId,
                                accessToken
                        )
        );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void givenGenericExceptionWhenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenThrowIt() {
        Long organizationId = 1L;
        String operatorExternalUserId = "OPERATOREXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";
        RuntimeException expectedException = new RuntimeException();

        Mockito.when(organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken)
        ).thenReturn(orgSubUnitSearchControllerApiMock);

        Mockito.when(
                orgSubUnitSearchControllerApiMock
                        .crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId
                        )
        ).thenThrow(expectedException);

        RuntimeException result = Assertions.assertThrows(
                expectedException.getClass(),
                () -> orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                organizationId,
                                operatorExternalUserId,
                                accessToken
                        )
        );

        Assertions.assertSame(expectedException, result);
    }
}