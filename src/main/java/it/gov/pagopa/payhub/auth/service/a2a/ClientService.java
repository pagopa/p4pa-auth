package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;

import java.util.List;
import java.util.Optional;

public interface ClientService {

	ClientDTO registerClient(String clientName, String organizationIpaCode);
	String getClientSecret(String organizationIpaCode, String clientId);
	List<ClientNoSecretDTO> getClients(String organizationIpaCode);
	Optional<Client> getClientByClientId(String clientId);
	ClientDTO verifyCredentials(String clientId, String clientSecret);
}
