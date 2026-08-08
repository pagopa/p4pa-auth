package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Broker;

public interface BrokerService {
    Broker getBrokerById(Long id, String accessToken);
}
