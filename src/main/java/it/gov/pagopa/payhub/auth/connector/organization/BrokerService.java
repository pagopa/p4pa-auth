package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;

public interface BrokerService {
    Broker getBrokerById(Long id, String accessToken);
}
