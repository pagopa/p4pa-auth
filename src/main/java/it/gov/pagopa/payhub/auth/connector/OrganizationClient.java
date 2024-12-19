package it.gov.pagopa.payhub.auth.connector;

import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelBroker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelOrganization;

public interface OrganizationClient {

    EntityModelBroker getBrokerById(Long id, String accessToken);

    EntityModelOrganization getOrganizationByIpaCode(String ipaCode, String accessToken);

}
