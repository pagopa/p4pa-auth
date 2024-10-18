package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorizeClientCredentialsRequestService {
	private final ClientService clientService;

	public AuthorizeClientCredentialsRequestService(ClientService clientService) {
		this.clientService = clientService;
	}

	public ClientDTO authorizeCredentials(String clientId, String clientSecret) {
		return clientService.authorizeCredentials(clientId, clientSecret);
	}
}
