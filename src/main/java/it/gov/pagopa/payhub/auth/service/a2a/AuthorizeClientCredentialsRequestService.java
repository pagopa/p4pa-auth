package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorizeClientCredentialsRequestService {
	private final ClientService clientService;
	private final ClientMapper clientMapper;

	public AuthorizeClientCredentialsRequestService(ClientService clientService, ClientMapper clientMapper) {
		this.clientService = clientService;
		this.clientMapper = clientMapper;
	}

	public ClientDTO authorizeCredentials(String clientId, String clientSecret) {
		return clientService.getClientByClientId(clientId)
			.map(clientMapper::mapToDTO)
			.filter(dto -> dto.getClientSecret().equals(clientSecret))
			.orElseThrow(() -> new ClientUnauthorizedException("Unauthorized client for client-credentials"));
	}
}
