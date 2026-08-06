package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;

public interface OrganizationService {
    Organization getOrganizationByIpaCode(String ipaCode, String accessToken);

    /**
     * Updates the organization's external id and evicts the cached Organization
     *
     * @param ipaCode the cache key used to evict the Organization entry
     */
    void updateExternalOrganizationId(Long organizationId, String organizationExternalId, String ipaCode, String accessToken);
}
