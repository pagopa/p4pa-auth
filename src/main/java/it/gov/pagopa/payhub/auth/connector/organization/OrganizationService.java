package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;

public interface OrganizationService {
    Organization getOrganizationByIpaCode(String ipaCode, String accessToken);
    void updateOrganizationExternalId(Long organizationId, String organizationExternalId, String accessToken);
}
