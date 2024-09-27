package it.gov.pagopa.payhub.auth.service.a2a.retreive;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.springframework.stereotype.Service;

@Service
public class ClientDTOMapper {

	public ClientDTO map(Client client) {
		return ClientDTO.builder()
			.clientId(client.getClientId())
			.organizationIpaCode(client.getOrganizationIpaCode())
			.clientSecret(client.getClientSecret())
			.build();
	}
}
