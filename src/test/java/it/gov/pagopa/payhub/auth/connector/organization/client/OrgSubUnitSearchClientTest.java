package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.OrgSubUnitSearchControllerApi;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.CollectionModelOrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.PagedModelOrgSubUnitEmbedded;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitSearchClientTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final String OPERATOR_EXTERNAL_USER_ID = "OPERATOREXTERNALUSERID";
    private static final String ACCESS_TOKEN = "ACCESSTOKEN";

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
        Mockito.verifyNoMoreInteractions(
                organizationApisHolder,
                orgSubUnitSearchControllerApiMock
        );
    }

    @Test
    void whenGetAllOrgSubUnitsByOrganizationIdThenReturnOrgSubUnitList() {
        List<OrgSubUnit> expectedResult = List.of(new OrgSubUnit().subUnitCode("SUB_UNIT_CODE"));
        CollectionModelOrgSubUnit response = buildResponse(expectedResult);

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenReturn(response);

        List<OrgSubUnit> result = orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNullResponseWhenGetAllOrgSubUnitsByOrganizationIdThenReturnEmptyList() {
        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenReturn(null);

        List<OrgSubUnit> result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenNullEmbeddedWhenGetAllOrgSubUnitsByOrganizationIdThenReturnEmptyList() {
        CollectionModelOrgSubUnit response = new CollectionModelOrgSubUnit();

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenReturn(response);

        List<OrgSubUnit> result = orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenNullOrgSubUnitsWhenGetAllOrgSubUnitsByOrganizationIdThenReturnEmptyList() {
        PagedModelOrgSubUnitEmbedded embedded =
                new PagedModelOrgSubUnitEmbedded();

        CollectionModelOrgSubUnit response =
                new CollectionModelOrgSubUnit()
                        .embedded(embedded);

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenReturn(response);

        List<OrgSubUnit> result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptyOrgSubUnitsWhenGetAllOrgSubUnitsByOrganizationIdThenReturnEmptyList() {
        CollectionModelOrgSubUnit response = buildResponse(Collections.emptyList());

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenReturn(response);

        List<OrgSubUnit> result =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenNotFoundWhenGetAllOrgSubUnitsByOrganizationIdThenPropagateException() {
        HttpClientErrorException expectedException =
                HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "NotFound",
                        null,
                        null,
                        null
                );

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenThrow(expectedException);

        HttpClientErrorException result =
                Assertions.assertThrows(
                        HttpClientErrorException.class,
                        () -> orgSubUnitSearchClient
                                .getAllOrgSubUnitsByOrganizationId(
                                        ORGANIZATION_ID,
                                        ACCESS_TOKEN
                                )
                );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void givenGenericExceptionWhenGetAllOrgSubUnitsByOrganizationIdThenPropagateException() {
        RuntimeException expectedException = new RuntimeException("Unexpected error");

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationId(ORGANIZATION_ID))
                .thenThrow(expectedException);

        RuntimeException result =
                Assertions.assertThrows(
                        RuntimeException.class,
                        () -> orgSubUnitSearchClient
                                .getAllOrgSubUnitsByOrganizationId(
                                        ORGANIZATION_ID,
                                        ACCESS_TOKEN
                                )
                );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void whenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenReturnOrgSubUnitList() {
        List<OrgSubUnit> expectedResult = List.of(new OrgSubUnit().subUnitCode("SUB_UNIT_CODE"));
        CollectionModelOrgSubUnit response = buildResponse(expectedResult);

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(ORGANIZATION_ID, OPERATOR_EXTERNAL_USER_ID))
                .thenReturn(response);

        List<OrgSubUnit> result =
                orgSubUnitSearchClient
                        .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                ORGANIZATION_ID,
                                OPERATOR_EXTERNAL_USER_ID,
                                ACCESS_TOKEN
                        );

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundWhenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenPropagateException() {
        HttpClientErrorException expectedException =
                HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "NotFound",
                        null,
                        null,
                        null
                );

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(ORGANIZATION_ID, OPERATOR_EXTERNAL_USER_ID))
                .thenThrow(expectedException);

        HttpClientErrorException result =
                Assertions.assertThrows(
                        HttpClientErrorException.class,
                        () -> orgSubUnitSearchClient
                                .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                        ORGANIZATION_ID,
                                        OPERATOR_EXTERNAL_USER_ID,
                                        ACCESS_TOKEN
                                )
                );

        Assertions.assertSame(expectedException, result);
    }

    @Test
    void givenGenericExceptionWhenGetAllOrgSubUnitsByOrganizationIdAndOperatorThenPropagateException() {
        RuntimeException expectedException =
                new RuntimeException("Unexpected error");

        when(organizationApisHolder.getOrgSubUnitSearchControllerApi(ACCESS_TOKEN))
                .thenReturn(orgSubUnitSearchControllerApiMock);

        when(orgSubUnitSearchControllerApiMock.crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(ORGANIZATION_ID, OPERATOR_EXTERNAL_USER_ID))
                .thenThrow(expectedException);

        RuntimeException result =
                Assertions.assertThrows(
                        RuntimeException.class,
                        () -> orgSubUnitSearchClient
                                .getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(
                                        ORGANIZATION_ID,
                                        OPERATOR_EXTERNAL_USER_ID,
                                        ACCESS_TOKEN
                                )
                );

        Assertions.assertSame(expectedException, result);
    }

    private CollectionModelOrgSubUnit buildResponse(List<OrgSubUnit> orgSubUnits) {
        PagedModelOrgSubUnitEmbedded embedded = new PagedModelOrgSubUnitEmbedded().orgSubUnits(orgSubUnits);

        return new CollectionModelOrgSubUnit().embedded(embedded);
    }
}