package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrgSubUnitSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.CollectionModelOrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnitStatus;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.PagedModelOrgSubUnitEmbedded;
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
    public List<OrgSubUnit> getOrgSubUnitsByOrganizationId(Long organizationId, String accessToken) {
        CollectionModelOrgSubUnit response =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationId(organizationId, accessToken);

        return extractActiveOrgSubUnits(response);
    }

    @Override
    public List<OrgSubUnit> getOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(Long organizationId, String operatorExternalUserId, String accessToken) {
        CollectionModelOrgSubUnit response =
                orgSubUnitSearchClient.getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId, accessToken);

        return extractActiveOrgSubUnits(response);
    }

    private List<OrgSubUnit> extractActiveOrgSubUnits(CollectionModelOrgSubUnit response) {
        if (response == null) {
            return Collections.emptyList();
        }

        PagedModelOrgSubUnitEmbedded embedded = response.getEmbedded();

        if (embedded == null || CollectionUtils.isEmpty(embedded.getOrgSubUnits())) {
            return Collections.emptyList();
        }

        return embedded.getOrgSubUnits().stream()
                .filter(Objects::nonNull)
                .filter(orgSubUnit -> OrgSubUnitStatus.ACTIVE.equals(orgSubUnit.getStatus()))
                .toList();
    }
}
