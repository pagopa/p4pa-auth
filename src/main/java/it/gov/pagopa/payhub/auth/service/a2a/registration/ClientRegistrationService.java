package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ClientRegistrationService {

	private final ClientSecretGeneratorService clientSecretGeneratorService;
	private final ClientRepository clientRepository;

	public ClientRegistrationService(ClientSecretGeneratorService clientSecretGeneratorService, ClientRepository clientRepository) {
		this.clientSecretGeneratorService = clientSecretGeneratorService;
		this.clientRepository = clientRepository;
	}

	public Client registerClient(String clientId, String organizationIpaCode) {
		String clientSecret = clientSecretGeneratorService.apply(UUID.randomUUID().toString());
		log.info("Registering client having clientId {} and organizationIpaCode {}", clientId, organizationIpaCode);
		return clientRepository.registerClient(clientId, organizationIpaCode, clientSecret);
	}
}
