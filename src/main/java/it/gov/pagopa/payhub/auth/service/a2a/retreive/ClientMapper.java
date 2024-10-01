package it.gov.pagopa.payhub.auth.service.a2a.retreive;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.springframework.stereotype.Service;

@Service
public class ClientMapper {

	private final DataCipherService dataCipherService;

	public ClientMapper(DataCipherService dataCipherService) {
		this.dataCipherService = dataCipherService;
	}

	public ClientDTO mapToDTO(Client client) {
		return ClientDTO.builder()
			.clientId(client.getClientId())
			.organizationIpaCode(client.getOrganizationIpaCode())
			.clientSecret(dataCipherService.decrypt(client.getClientSecret()))
			.build();
	}

	public Client mapToModel(String clientId, String organizationIpaCode, String clientSecret) {
		return Client.builder()
			.clientId(clientId)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(dataCipherService.encrypt(clientSecret))
			.build();
	}

	public Client mapToModel(ClientDTO clientDTO) {
		return mapToModel(clientDTO.getClientId(), clientDTO.getOrganizationIpaCode(), clientDTO.getClientSecret());
	}
}
