package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.*;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthzService {
    Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest);
    Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, String fiscalCode, String firstName, String lastName, Pageable pageRequest);
    OperatorDTO getOrganizationOperator(String organizationIpaCode, String mappedExternalUserId);
    void deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId);
    OperatorDTO createOrganizationOperator(String organizationIpaCode, CreateOperatorRequest createOperatorRequest);
    UserDTO createUser(UserDTO user);
    UserInfo getUserInfoFromMappedExternalUserId(String mappedExternalUserId);
    ClientDTO registerClient(String organizationIpaCode, CreateClientRequest createClientRequest);
    String getClientSecret(String organizationIpaCode, String clientId);
}
