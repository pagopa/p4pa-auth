package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	private final ClientRegistrationService clientRegistrationService;

	public ClientServiceImpl(ClientRegistrationService clientRegistrationService) {
		this.clientRegistrationService = clientRegistrationService;
	}

	@Override
	public Client registerClient(String clientId, String organizationIpaCode) {
		return clientRegistrationService.registerClient(clientId, organizationIpaCode);
	}
}
