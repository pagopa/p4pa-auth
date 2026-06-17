package it.gov.pagopa.payhub.auth.connector.debtposition.config;

import it.gov.pagopa.payhub.auth.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgOperatorsApi;
import it.gov.pagopa.pu.debtposition.generated.ApiClient;
import it.gov.pagopa.pu.debtposition.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class DebtPositionApisHolder {

    private final DebtPositionTypeOrgOperatorsApi debtPositionTypeOrgOperatorsApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public DebtPositionApisHolder(
            DebtPositionApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("DEBT-POSITIONS"));
        }

        this.debtPositionTypeOrgOperatorsApi = new DebtPositionTypeOrgOperatorsApi(apiClient);
    }

    @PreDestroy
    public void unload() {
        bearerTokenHolder.remove();
    }

    /**
     * It will return a {@link DebtPositionTypeOrgOperatorsApi} instrumented with the provided accessToken. Use null if auth is not required
     */
    public DebtPositionTypeOrgOperatorsApi getDebtPositionTypeOrgOperatorsApi(String accessToken) {
        return getApi(accessToken, debtPositionTypeOrgOperatorsApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }

}
