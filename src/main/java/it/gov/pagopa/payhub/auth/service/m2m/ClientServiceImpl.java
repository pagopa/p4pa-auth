package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.m2m.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.m2m.retrieve.ClientRetrieverService;
import it.gov.pagopa.payhub.auth.service.m2m.revoke.ClientRemovalService;
import it.gov.pagopa.payhub.dto.generated.ClientDTO;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
	private final ClientRemovalService clientRemovalService;
	private final ClientRegistrationService clientRegistrationService;
	private final ClientRetrieverService clientRetrieverService;
	private final ClientMapper clientMapper;

	public ClientServiceImpl(ClientRemovalService clientRemovalService, ClientRegistrationService clientRegistrationService, ClientRetrieverService clientRetrieverService, ClientMapper clientMapper) {
		this.clientRemovalService = clientRemovalService;
		this.clientRegistrationService = clientRegistrationService;
		this.clientRetrieverService = clientRetrieverService;
		this.clientMapper = clientMapper;
	}

	@Override
	public ClientDTO registerClient(String clientName, String organizationIpaCode) {
		Client client = clientRegistrationService.registerClient(clientName, organizationIpaCode);
		return clientMapper.mapToDTO(client);
	}

	@Override
	public Optional<Client> getClient(String organizationIpaCode, String clientId) {
		log.info("Retrieving client having organizationIpaCode {} and ID {}", organizationIpaCode, clientId);
		return clientRetrieverService.getClient(organizationIpaCode, clientId);
	}

	@Override
	public List<ClientNoSecretDTO> getClients(String organizationIpaCode) {
		log.info("Retrieving clients for {}", organizationIpaCode);
		return clientRetrieverService.getClients(organizationIpaCode);
	}

	public Optional<Client> getClientByClientId(String clientId) {
		log.info("Retrieving client for {}", clientId);
		return clientRetrieverService.getClientByClientId(clientId);
	}

	@Override
	public void revokeClient(String organizationIpaCode, String clientId) {
		clientRemovalService.revokeClient(organizationIpaCode, clientId);
	}

}
