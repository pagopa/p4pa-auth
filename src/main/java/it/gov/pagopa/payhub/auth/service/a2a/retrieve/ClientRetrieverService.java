package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientRetrieverService {

    private final ClientRepository clientRepository;

    public ClientRetrieverService(ClientRepository clientRepository) {
	    this.clientRepository = clientRepository;
    }

    public byte[] getClientSecret(String organizationIpaCode, String clientId) {
      return clientRepository.findById(clientId)
        .filter(client -> client.getOrganizationIpaCode().equals(organizationIpaCode))
        .map(Client::getClientSecret)
        .orElseThrow(() -> new ClientNotFoundException("Client not found"));
  }
}
