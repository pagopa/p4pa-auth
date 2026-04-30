package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class BrokerClient {

    private final OrganizationApisHolder organizationApisHolder;

    public BrokerClient(OrganizationApisHolder organizationApisHolder) {
        this.organizationApisHolder = organizationApisHolder;
    }

    public Broker getBrokerById(Long id, String accessToken) {
        try {
            return organizationApisHolder.getBrokerEntityControllerApi(accessToken).crudGetBroker(String.valueOf(id));
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Broker with ID {} not found.", id);
            return null;
        }
    }
}
