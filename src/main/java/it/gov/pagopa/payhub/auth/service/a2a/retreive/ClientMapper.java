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


	public ClientDTO mapToDTO(String clientId, String clientName, String organizationIpaCode, byte[] clientSecret) {
		return ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(dataCipherService.decrypt(clientSecret))
			.build();
	}

	public ClientDTO mapToDTO(Client client) {
		return mapToDTO(client.getClientId(), client.getClientName(), client.getOrganizationIpaCode(), client.getClientSecret());
	}

	public Client mapToModel(String clientName, String organizationIpaCode, String clientSecret) {
		return Client.builder()
			.clientId(organizationIpaCode + clientName)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(dataCipherService.encrypt(clientSecret))
			.build();
	}

	public Client mapToModel(ClientDTO clientDTO) {
		return mapToModel(clientDTO.getClientName(), clientDTO.getOrganizationIpaCode(), clientDTO.getClientSecret());
	}
}
