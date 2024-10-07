package it.gov.pagopa.payhub.auth.mapper;

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
			.clientName(client.getClientName())
			.organizationIpaCode(client.getOrganizationIpaCode())
			.clientSecret(dataCipherService.decrypt(client.getClientSecret()))
			.build();
	}

	public Client mapToModel(ClientDTO clientDTO) {
		return Client.builder()
			.clientId(clientDTO.getClientId())
			.clientName(clientDTO.getClientName())
			.organizationIpaCode(clientDTO.getOrganizationIpaCode())
			.clientSecret(dataCipherService.encrypt(clientDTO.getClientSecret()))
			.build();
	}

}
