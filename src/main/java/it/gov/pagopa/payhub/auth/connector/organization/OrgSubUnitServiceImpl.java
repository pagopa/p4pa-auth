package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrgSubUnitSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnitStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class OrgSubUnitServiceImpl implements OrgSubUnitService {

    private final OrgSubUnitSearchClient orgSubUnitSearchClient;

    public OrgSubUnitServiceImpl(OrgSubUnitSearchClient orgSubUnitSearchClient) {
        this.orgSubUnitSearchClient = orgSubUnitSearchClient;
    }

    @Override
    public List<OrgSubUnit> getActiveOrgSubUnitsByOrganizationId(Long organizationId, String accessToken) {
        List<OrgSubUnit> orgSubUnits =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(organizationId, accessToken);

        return extractActiveOrgSubUnits(orgSubUnits);
    }

    @Override
    public List<OrgSubUnit> getActiveOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(Long organizationId, String operatorExternalUserId, String accessToken) {
        List<OrgSubUnit> orgSubUnits =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId, accessToken);

        return extractActiveOrgSubUnits(orgSubUnits);
    }

    private List<OrgSubUnit> extractActiveOrgSubUnits(List<OrgSubUnit> orgSubUnits) {
        if (CollectionUtils.isEmpty(orgSubUnits)) {
            return Collections.emptyList();
        }

        return orgSubUnits.stream()
                .filter(Objects::nonNull)
                .filter(orgSubUnit -> OrgSubUnitStatus.ACTIVE.equals(orgSubUnit.getStatus()))
                .toList();
    }
}
