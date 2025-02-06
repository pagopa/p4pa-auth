package it.gov.pagopa.payhub.auth.connector.organization.config;

import it.gov.pagopa.pu.p4pa_organization.controller.ApiClient;
import it.gov.pagopa.pu.p4pa_organization.controller.BaseApi;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.OrganizationSearchControllerApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Lazy
@Service
public class OrganizationApisHolder {

    private final BrokerEntityControllerApi brokerEntityControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public OrganizationApisHolder(
            @Value("${rest.organization.base-url}") String baseUrl,

            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

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
