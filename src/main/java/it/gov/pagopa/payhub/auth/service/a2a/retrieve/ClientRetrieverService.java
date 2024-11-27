package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientRetrieverService {

	private final DataCipherService dataCipherService;

	private final ClientRepository clientRepository;

	private final ClientMapper clientMapper;

	public ClientRetrieverService(DataCipherService dataCipherService, ClientRepository clientRepository, ClientMapper clientMapper) {
		this.dataCipherService = dataCipherService;
		this.clientRepository = clientRepository;
		this.clientMapper = clientMapper;
	}

	public String getClientSecret(String organizationIpaCode, String clientId) {
		return clientRepository.findById(clientId)
			.filter(client -> client.getOrganizationIpaCode().equals(organizationIpaCode))
			.map(Client::getClientSecret)
			.map(dataCipherService::decrypt)
			.orElseThrow(() -> new ClientNotFoundException("Cannot found client secret having clientId:"+ clientId +"for organizationIpaCode:"+organizationIpaCode));
	}

	public List<ClientNoSecretDTO> getClients(String organizationIpaCode) {
		return clientRepository.findAllByOrganizationIpaCode(organizationIpaCode).stream()
			.map(clientMapper::mapToNoSecretDTO)
			.toList();
	}

	public Optional<Client> getClientByClientId(String clientId) { return clientRepository.findById(clientId); }

}
