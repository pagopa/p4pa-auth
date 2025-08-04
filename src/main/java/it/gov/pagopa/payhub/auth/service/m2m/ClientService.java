package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.dto.generated.ClientDTO;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    ClientDTO registerClient(String clientName, String organizationIpaCode);

    Optional<ClientDTO> getClient(String organizationIpaCode, String clientId);

    List<ClientNoSecretDTO> getClients(String organizationIpaCode);

    Optional<Client> getClientByClientId(String clientId);

    void revokeClient(String organizationIpaCode, String clientId);
}
