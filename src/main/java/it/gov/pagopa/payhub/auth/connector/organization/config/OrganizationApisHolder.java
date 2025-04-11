package it.gov.pagopa.payhub.auth.connector.organization.config;

import it.gov.pagopa.payhub.auth.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.p4pa_organization.controller.ApiClient;
import it.gov.pagopa.pu.p4pa_organization.controller.BaseApi;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.OrganizationSearchControllerApi;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OrganizationApisHolder {

    private final BrokerEntityControllerApi brokerEntityControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public OrganizationApisHolder(
            OrganizationApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("ORGANIZATION"));
        }

        this.organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
        this.brokerEntityControllerApi = new BrokerEntityControllerApi(apiClient);
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

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }

}
