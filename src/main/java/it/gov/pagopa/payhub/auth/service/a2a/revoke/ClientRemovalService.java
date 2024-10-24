package it.gov.pagopa.payhub.auth.service.a2a.revoke;

import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientRemovalService {

	private final ClientRepository clientRepository;

	public ClientRemovalService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Transactional
	public void revokeClient(String organizationIpaCode, String clientId) {
		clientRepository.deleteClient(organizationIpaCode, clientId);
	}
}
