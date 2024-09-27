package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;

public interface ClientService {

	Client registerClient(String clientId, String organizationIpaCode);
}
