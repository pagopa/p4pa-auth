package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.CollectionModelOrgSubUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class OrgSubUnitSearchClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrgSubUnitSearchClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public CollectionModelOrgSubUnit getAllOrgSubUnitsByOrganizationId(Long organizationId, String accessToken) {
        try {
            return organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken)
                    .crudOrgSubUnitFindAllByOrganizationId(organizationId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("OrgSubUnit with organizationId {} not found", organizationId);
            return null;
        }
    }

    public CollectionModelOrgSubUnit getAllOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(Long organizationId, String operatorExternalUserId, String accessToken) {
        try {
            return organizationApisHolder.getOrgSubUnitSearchControllerApi(accessToken)
                    .crudOrgSubUnitFindAllByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("OrgSubUnit with organizationId {} not found", organizationId);
            return null;
        }
    }
}
