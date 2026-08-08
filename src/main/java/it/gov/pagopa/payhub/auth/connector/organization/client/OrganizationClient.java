package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class OrganizationClient {

  private final OrganizationApisHolder organizationApisHolder;

  public OrganizationClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public void updateExternalOrganizationId(Long organizationId, String organizationExternalId, String accessToken) {
    try {
      organizationApisHolder.getOrganizationApi(accessToken)
              .updateOrganizationExternalId(organizationId, organizationExternalId);
    } catch (HttpClientErrorException.NotFound e) {
      throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND", "Organization with organizationId " + organizationId + " not found");
    }
  }
}
