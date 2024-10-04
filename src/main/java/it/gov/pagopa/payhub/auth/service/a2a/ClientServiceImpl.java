package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientRetrieverService;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	private final ClientRegistrationService clientRegistrationService;

	private final ClientRetrieverService clientRetrieverService;

	private final DataCipherService dataCipherService;

	private final ClientMapper clientMapper;

	public ClientServiceImpl(ClientRegistrationService clientRegistrationService, ClientRetrieverService clientRetrieverService, DataCipherService dataCipherService, ClientMapper clientMapper) {
		this.clientRegistrationService = clientRegistrationService;
		this.clientRetrieverService = clientRetrieverService;
		this.dataCipherService = dataCipherService;
		this.clientMapper = clientMapper;
	}

	@Override
	public ClientDTO registerClient(String clientName, String organizationIpaCode) {
		Client client = clientRegistrationService.registerClient(clientName, organizationIpaCode);
		return clientMapper.mapToDTO(client);
	}

	@Override
	public String getClientSecret(String organizationIpaCode, String clientId) {
		byte[] clientSecret = clientRetrieverService.getClientSecret(organizationIpaCode, clientId);
		return dataCipherService.decrypt(clientSecret);
	}

}
