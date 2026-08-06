package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.OrganizationClient;
import it.gov.pagopa.payhub.auth.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = it.gov.pagopa.payhub.auth.config.CacheConfig.Fields.organization)
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationSearchClient searchClient;
    private final OrganizationClient organizationClient;

    public OrganizationServiceImpl(OrganizationSearchClient searchClient, OrganizationClient organizationClient) {
        this.searchClient = searchClient;
        this.organizationClient = organizationClient;
    }

    @Override
    @Cacheable(key = "#ipaCode", unless="#result == null")
    public Organization getOrganizationByIpaCode(String ipaCode, String accessToken) {
        return searchClient.getOrganizationByIpaCode(ipaCode, accessToken);
    }

    @Override
    @CacheEvict(key = "#ipaCode")
    public void updateExternalOrganizationId(Long organizationId, String organizationExternalId, String ipaCode, String accessToken) {
        organizationClient.updateExternalOrganizationId(organizationId, organizationExternalId, accessToken);
    }
}
