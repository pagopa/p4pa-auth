package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrgSubUnitSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnitStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgSubUnitServiceTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final String OPERATOR_EXTERNAL_USER_ID = "operatorExternalUserId";
    private static final String ACCESS_TOKEN = "accessToken";

    @Mock
    private OrgSubUnitSearchClient orgSubUnitSearchClientMock;

    @InjectMocks
    private OrgSubUnitServiceImpl orgSubUnitService;

    @BeforeEach
    void setUp() {
        orgSubUnitService = new OrgSubUnitServiceImpl(orgSubUnitSearchClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(orgSubUnitSearchClientMock);
    }

    @Test
    void getOrgSubUnitsByOrganizationIdShouldReturnOnlyActiveOrgSubUnits() {
        OrgSubUnit activeOrgSubUnit = buildOrgSubUnit(
                "ACTIVE_CODE",
                OrgSubUnitStatus.ACTIVE
        );
        OrgSubUnit inactiveOrgSubUnit = buildOrgSubUnit(
                "CANCELLED_CODE",
                OrgSubUnitStatus.CANCELLED
        );

        when(orgSubUnitSearchClientMock.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN))
                .thenReturn(List.of(activeOrgSubUnit, inactiveOrgSubUnit));

        List<OrgSubUnit> result = orgSubUnitService.getActiveOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(List.of(activeOrgSubUnit), result);
    }

    @Test
    void getOrgSubUnitsByOrganizationIdAndOperatorExternalUserIdShouldReturnOnlyActiveOrgSubUnits() {
        OrgSubUnit firstActiveOrgSubUnit = buildOrgSubUnit(
                "FIRST_ACTIVE_CODE",
                OrgSubUnitStatus.ACTIVE
        );
        OrgSubUnit secondActiveOrgSubUnit = buildOrgSubUnit(
                "SECOND_ACTIVE_CODE",
                OrgSubUnitStatus.ACTIVE
        );
        OrgSubUnit inactiveOrgSubUnit = buildOrgSubUnit(
                "CANCELLED_CODE",
                OrgSubUnitStatus.CANCELLED
        );

        when(orgSubUnitSearchClientMock.getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(ORGANIZATION_ID, OPERATOR_EXTERNAL_USER_ID, ACCESS_TOKEN))
                .thenReturn(List.of(firstActiveOrgSubUnit, inactiveOrgSubUnit, secondActiveOrgSubUnit));

        List<OrgSubUnit> result = orgSubUnitService.getActiveOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(ORGANIZATION_ID, OPERATOR_EXTERNAL_USER_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(List.of(firstActiveOrgSubUnit, secondActiveOrgSubUnit), result);
    }

    @Test
    void getOrgSubUnitsByOrganizationIdShouldReturnEmptyListWhenClientReturnsNull() {
        when(orgSubUnitSearchClientMock.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN))
                .thenReturn(null);

        List<OrgSubUnit> result = orgSubUnitService.getActiveOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getOrgSubUnitsByOrganizationIdShouldReturnEmptyListWhenClientReturnsEmptyList() {
        when(orgSubUnitSearchClientMock.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN))
                .thenReturn(Collections.emptyList());

        List<OrgSubUnit> result = orgSubUnitService.getActiveOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getOrgSubUnitsByOrganizationIdShouldIgnoreNullElements() {
        OrgSubUnit activeOrgSubUnit = buildOrgSubUnit(
                "ACTIVE_CODE",
                OrgSubUnitStatus.ACTIVE
        );

        when(orgSubUnitSearchClientMock.getAllOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN))
                .thenReturn(listContainingNullAnd(activeOrgSubUnit));

        List<OrgSubUnit> result = orgSubUnitService.getActiveOrgSubUnitsByOrganizationId(ORGANIZATION_ID, ACCESS_TOKEN);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(List.of(activeOrgSubUnit), result);
    }

    private OrgSubUnit buildOrgSubUnit(String orgSubUnitCode, OrgSubUnitStatus status) {
        return new OrgSubUnit()
                .subUnitCode(orgSubUnitCode)
                .status(status);
    }

    private List<OrgSubUnit> listContainingNullAnd(OrgSubUnit orgSubUnit) {
        List<OrgSubUnit> result = new ArrayList<>();
        result.add(null);
        result.add(orgSubUnit);
        return result;
    }
}