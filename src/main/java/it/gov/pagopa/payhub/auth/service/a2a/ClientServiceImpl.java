package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	private final ClientRegistrationService clientRegistrationService;

	private final ClientMapper clientMapper;

	public ClientServiceImpl(ClientRegistrationService clientRegistrationService, ClientMapper clientMapper) {
		this.clientRegistrationService = clientRegistrationService;
		this.clientMapper = clientMapper;
	}

	@Override
	public ClientDTO registerClient(String clientId, String organizationIpaCode) {
		Client client = clientRegistrationService.registerClient(clientId, organizationIpaCode);
		return clientMapper.mapToDTO(client);
	}
}
