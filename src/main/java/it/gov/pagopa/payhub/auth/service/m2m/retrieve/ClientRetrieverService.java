package it.gov.pagopa.payhub.auth.service.m2m.retrieve;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ClientRetrieverService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    public ClientRetrieverService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public Optional<Client> getClient(String organizationIpaCode, String clientId) {
        return clientRepository.findById(clientId)
                .filter(client -> client.getOrganizationIpaCode().equals(organizationIpaCode));
    }

    public List<ClientNoSecretDTO> getClients(String organizationIpaCode) {
        return clientRepository.findAllByOrganizationIpaCode(organizationIpaCode).stream()
                .map(clientMapper::mapToNoSecretDTO)
                .toList();
    }

    public Optional<Client> getClientByClientId(String clientId) {
        return clientRepository.findById(clientId);
    }

}
