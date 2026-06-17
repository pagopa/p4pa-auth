package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.dto.generated.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AuthzService {
    Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest);

    Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, String fiscalCode, String firstName, String lastName, Pageable pageRequest);

    OperatorDTO getOrganizationOperator(String organizationIpaCode, String mappedExternalUserId);

    void deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId);

    void deleteOrganizationOperatorByExternalUserId(String organizationIpaCode, String externalUserId);

    OperatorDTO createOrganizationOperator(String organizationIpaCode, CreateOperatorRequest createOperatorRequest, String accessToken);

    UserDTO createUser(UserDTO user);

    UserInfo getUserInfoFromMappedExternalUserId(String mappedExternalUserId, String accessToken);

    ClientDTO registerClient(String organizationIpaCode, CreateClientRequest createClientRequest);

    Optional<ClientDTO> getClient(String organizationIpaCode, String clientId);

    List<ClientNoSecretDTO> getClients(String organizationIpaCode);

    void revokeClient(String organizationIpaCode, String clientId);

    Page<ClientNoSecretDTO> getClientsSearch(String clientId, String clientName, String organizationIpaCode, Pageable pageRequest);

    Optional<ClientDTO> generateClientSecret(String organizationIpaCode, String clientId);
}
