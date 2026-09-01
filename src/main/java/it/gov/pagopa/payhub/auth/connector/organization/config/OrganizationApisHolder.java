package it.gov.pagopa.payhub.auth.connector.organization.config;

import it.gov.pagopa.payhub.auth.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.payhub.auth.connector.organization.mapper.OrganizationErrorDTOMapper;
import it.gov.pagopa.pu.organization.generated.ApiClient;
import it.gov.pagopa.pu.organization.generated.BaseApi;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrgSubUnitSearchControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
public class OrganizationApisHolder {

    private final BrokerEntityControllerApi brokerEntityControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;
    private final OrganizationApi organizationApi;
    private final OrgSubUnitSearchControllerApi orgSubUnitSearchControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public OrganizationApisHolder(
            OrganizationApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder,
            JsonMapper jsonMapper
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "ORGANIZATION", clientConfig.isPrintBodyWhenError(),
                OrganizationErrorDTO.class, OrganizationErrorDTOMapper::map)
        );

        this.organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
        this.brokerEntityControllerApi = new BrokerEntityControllerApi(apiClient);
        this.organizationApi = new OrganizationApi(apiClient);
        this.orgSubUnitSearchControllerApi = new OrgSubUnitSearchControllerApi(apiClient);
    }

    @PreDestroy
    public void unload() {
        bearerTokenHolder.remove();
    }

    /**
     * It will return a {@link OrganizationSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public OrganizationSearchControllerApi getOrganizationSearchControllerApi(String accessToken) {
        return getApi(accessToken, organizationSearchControllerApi);
    }

    /**
     * It will return a {@link BrokerEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public BrokerEntityControllerApi getBrokerEntityControllerApi(String accessToken) {
        return getApi(accessToken, brokerEntityControllerApi);
    }

    /**
     * It will return a {@link OrganizationApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public OrganizationApi getOrganizationApi(String accessToken){
        return getApi(accessToken, organizationApi);
    }

    /**
     * It will return a {@link OrgSubUnitSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public OrgSubUnitSearchControllerApi getOrgSubUnitSearchControllerApi(String accessToken) {
        return getApi(accessToken, orgSubUnitSearchControllerApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }

}
