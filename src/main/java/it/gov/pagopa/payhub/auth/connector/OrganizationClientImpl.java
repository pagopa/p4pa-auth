package it.gov.pagopa.payhub.auth.connector;

import it.gov.pagopa.pu.p4pa_organization.controller.ApiClient;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelBroker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelOrganization;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class OrganizationClientImpl implements OrganizationClient {

    private final BrokerEntityControllerApi brokerEntityControllerApi;
    private final OrganizationSearchControllerApi organizationSearchControllerApi;

    public OrganizationClientImpl(RestTemplateBuilder restTemplateBuilder, @Value("${app.organization.base-url}") String baseUrl) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        brokerEntityControllerApi = new BrokerEntityControllerApi(apiClient);
        organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
    }


    public EntityModelBroker getBrokerById(Long id, String accessToken) {
        try {
            return brokerEntityControllerApi.getItemResourceBrokerGet(String.valueOf(id));
        } catch (Exception e) {
            log.error(e.getCause());
            return null;
        }
    }

    public EntityModelOrganization getOrganizationByIpaCode(String ipaCode, String accessToken) {
        try {
            return organizationSearchControllerApi.executeSearchOrganizationGet(ipaCode);
        } catch (Exception e) {
            log.error(e.getCause());
            return null;
        }
    }

}
