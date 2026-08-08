package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrgSubUnit;
import it.gov.pagopa.pu.organization.dto.generated.OrgSubUnit;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrgSubUnitSearchClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrgSubUnitSearchClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public List<OrgSubUnit> getAllOrgSubUnitsByOrganizationId(Long organizationId, String accessToken) {
        CollectionModelOrgSubUnit response = organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken)
                .crudOrgSubUnitFindAllByOrganizationId(organizationId);

        return extractOrgSubUnits(response);
    }

    public List<OrgSubUnit> getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(Long organizationId, String operatorExternalUserId, String accessToken) {
        CollectionModelOrgSubUnit response = organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken)
                .crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId);

        return extractOrgSubUnits(response);
    }

    private List<OrgSubUnit> extractOrgSubUnits(CollectionModelOrgSubUnit response) {
        if (response == null || response.getEmbedded() == null) {
            return Collections.emptyList();
        }

        List<OrgSubUnit> orgSubUnits = response.getEmbedded().getOrgSubUnits();

        return orgSubUnits != null ? orgSubUnits : Collections.emptyList();
    }
}
