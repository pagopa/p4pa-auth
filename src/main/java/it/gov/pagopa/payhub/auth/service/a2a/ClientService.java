package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.model.generated.ClientDTO;

public interface ClientService {

	ClientDTO registerClient(String clientId, String organizationIpaCode);
	String getClientSecret(String organizationIpaCode, String clientId);
}
