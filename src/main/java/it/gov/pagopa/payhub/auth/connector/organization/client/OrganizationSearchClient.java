package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class OrganizationSearchClient {

    private final OrganizationApisHolder organizationApisHolder;

    public OrganizationSearchClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public Organization getOrganizationByIpaCode(String ipaCode, String accessToken) {
        try {
            return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                    .crudOrganizationsFindByIpaCode(ipaCode);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Organization with IPA code {} not found", ipaCode);
                return null;
            }
            log.error("Error retrieving organization by IPA code: {}", ipaCode, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving organization by IPA code: {}", ipaCode, e);
            throw e;
        }
    }

}
