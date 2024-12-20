package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retrieve.ClientRetrieverService;
import it.gov.pagopa.payhub.auth.service.a2a.revoke.ClientRemovalService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;
import jakarta.transaction.Transactional;
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
	public String getClientSecret(String organizationIpaCode, String clientId) {
		log.info("Retrieving client secret");
		return clientRetrieverService.getClientSecret(organizationIpaCode, clientId);
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
