package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrganizationClient {

  private final OrganizationApisHolder organizationApisHolder;

  public OrganizationClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public void updateExternalOrganizationId(Long organizationId, String organizationExternalId, String accessToken) {
      organizationApisHolder.getOrganizationApi(accessToken)
              .updateOrganizationExternalId(organizationId, organizationExternalId);
  }
}
