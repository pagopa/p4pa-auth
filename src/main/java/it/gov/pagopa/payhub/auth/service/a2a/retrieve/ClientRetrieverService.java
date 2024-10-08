package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientRetrieverService {

		private final DataCipherService dataCipherService;

    private final ClientRepository clientRepository;

    public ClientRetrieverService(DataCipherService dataCipherService, ClientRepository clientRepository) {
	    this.dataCipherService = dataCipherService;
	    this.clientRepository = clientRepository;
    }

    public String getClientSecret(String organizationIpaCode, String clientId) {
      return clientRepository.findById(clientId)
        .filter(client -> client.getOrganizationIpaCode().equals(organizationIpaCode))
        .map(Client::getClientSecret)
	      .map(dataCipherService::decrypt)
        .orElseThrow(() -> new ClientNotFoundException("Client not found"));
  }
}
