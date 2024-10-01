package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ClientRegistrationService {

	private final ClientMapper clientMapper;
	private final ClientRepository clientRepository;

	public ClientRegistrationService(ClientMapper clientMapper, ClientRepository clientRepository) {
		this.clientMapper = clientMapper;
		this.clientRepository = clientRepository;
	}

	public Client registerClient(String clientId, String organizationIpaCode) {
		Client client = clientMapper.mapToModel(clientId, organizationIpaCode, UUID.randomUUID().toString());
		log.info("Registering client having clientId {} and organizationIpaCode {}", clientId, organizationIpaCode);
		return clientRepository.insert(client);
	}
}
