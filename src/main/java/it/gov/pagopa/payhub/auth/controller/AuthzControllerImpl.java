package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.controller.generated.AuthzApi;
import it.gov.pagopa.payhub.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class AuthzControllerImpl implements AuthzApi {

    private final AuthzService authzService;
    private final boolean organizationAccessMode;

    public AuthzControllerImpl(AuthzService authzService,
        @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode) {
        this.authzService = authzService;
      this.organizationAccessMode = organizationAccessMode;
    }

    @Override
    public ResponseEntity<OperatorsPage> getOrganizationOperators(String organizationIpaCode, String fiscalCode, String firstName, String lastName, Integer page, Integer size) {
        log.info("Requesting organization operators of orgIpaCode {}", organizationIpaCode);
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to retrieve the operator list for organization " + organizationIpaCode);
        }

        Page<OperatorDTO> organizationOperators;
        if(StringUtils.hasLength(fiscalCode)
            || StringUtils.hasLength(firstName)
            || StringUtils.hasLength(lastName)
        ){
           organizationOperators = authzService.getOrganizationOperators(organizationIpaCode, fiscalCode, firstName, lastName, PageRequest.of(page, size));
        }
        else
            organizationOperators = authzService.getOrganizationOperators(organizationIpaCode, PageRequest.of(page, size));
        return ResponseEntity.ok(OperatorsPage.builder()
                .content(organizationOperators.getContent())
                .pageNo(page)
                .pageSize(size)
                .totalElements(organizationOperators.getNumberOfElements())
                .totalPages(organizationOperators.getTotalPages())
                .build());
    }

    @Override
    public ResponseEntity<UserInfo> getUserInfoFromMappedExternaUserId(String mappedExternalUserId) {
        log.info("Requesting UserInfo of mappedExternalUserId {}", mappedExternalUserId);
        String accessToken = SecurityUtils.getAccessToken();
        UserInfo loggedUser = SecurityUtils.getPrincipal();
        if(loggedUser.getMappedExternalUserId().equals(mappedExternalUserId)){
            return ResponseEntity.ok(loggedUser);
        }
        if(!SecurityUtils.hasAdminRole()){
            throw new UserUnauthorizedException("User not allowed to retrieve these information");
        }
        return ResponseEntity.ok(authzService.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken));
    }

    @Override
    public ResponseEntity<OperatorDTO> createOrganizationOperator(String organizationIpaCode,
        CreateOperatorRequest createOperatorRequest) {
        log.info("Adding operator to orgIpaCode {}: {}", organizationIpaCode, createOperatorRequest.getExternalUserId());
        if(organizationAccessMode){
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to create operator");
        }
        OperatorDTO operatorDTO = authzService.createOrganizationOperator(organizationIpaCode, createOperatorRequest);
        return ResponseEntity.ok(operatorDTO);
    }

    @Override
    public ResponseEntity<Void> deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId) {
        log.info("Deleting operator to orgIpaCode {}: {}", organizationIpaCode, mappedExternalUserId);
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to delete operator with mappedExternalUserId " + mappedExternalUserId);
        }
        authzService.deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<OperatorDTO> getOrganizationOperator(String organizationIpaCode, String mappedExternalUserId) {
        log.info("Retrieving operator of orgIpaCode {}: {}", organizationIpaCode, mappedExternalUserId);
        return ResponseEntity.ok(authzService.getOrganizationOperator(organizationIpaCode, mappedExternalUserId));
    }

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO user) {
        log.info("Creating user {}", user.getExternalUserId());
        if(organizationAccessMode){
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
        if(!SecurityUtils.hasAdminRole()){
            throw new UserUnauthorizedException("User not allowed to create user");
        }
        return ResponseEntity.ok(authzService.createUser(user));
    }

    @Override
    public ResponseEntity<ClientDTO> registerClient(String organizationIpaCode, CreateClientRequest createClientRequest) {
        log.info("Creating client on orgIpaCode {}: {}", organizationIpaCode, createClientRequest.getClientName());
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to create client");
        }
        return ResponseEntity.ok(authzService.registerClient(organizationIpaCode, createClientRequest));
    }

    @Override
    public ResponseEntity<String> getClientSecret(String organizationIpaCode, String clientId) {
        log.info("Retrieving clientSecret of client {} of orgIpaCode {}", clientId, organizationIpaCode);
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to retrieve client secret");
        }
        return ResponseEntity.ok(authzService.getClientSecret(organizationIpaCode, clientId));
    }

    @Override
    public ResponseEntity<List<ClientNoSecretDTO>> getClients(String organizationIpaCode) {
        log.info("Retrieving clients of orgIpaCode {}", organizationIpaCode);
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to retrieve the list of clients");
        }
        return ResponseEntity.ok(authzService.getClients(organizationIpaCode));
    }

    @Override
    public ResponseEntity<ClientDTOPage> getClientsSearch(String organizationIpaCode,
        String clientId, String clientName, Pageable pageable) {
        log.info("Requesting clients by filters");

        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to retrieve the list of clients");
        }

        Page<ClientNoSecretDTO> clients = authzService.getClientsSearch(clientId, clientName, organizationIpaCode,
            pageable);
        return ResponseEntity.ok(ClientDTOPage.builder()
            .content(clients.getContent())
            .pageNo(pageable.getPageNumber())
            .pageSize(pageable.getPageSize())
            .totalElements(clients.getNumberOfElements())
            .totalPages(clients.getTotalPages())
            .build());
    }

    @Override
    public ResponseEntity<Void> revokeClient(String organizationIpaCode, String clientId) {
        log.info("Deleting client on orgIpaCode {}: {}", organizationIpaCode, clientId);
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to delete client with clientId " + clientId);
        }
        authzService.revokeClient(organizationIpaCode, clientId);
        return ResponseEntity.ok(null);
    }
}
