package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.controller.generated.AuthzApi;
import it.gov.pagopa.payhub.model.generated.CreateOperatorRequest;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.OperatorsPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<OperatorsPage> getOrganizationOperators(String organizationIpaCode, Integer page, Integer size) {
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to retrieve the operator list for organization " + organizationIpaCode);
        }
        Page<OperatorDTO> organizationOperators = authzService.getOrganizationOperators(organizationIpaCode, PageRequest.of(page, size));
        return ResponseEntity.ok(OperatorsPage.builder()
                .content(organizationOperators.getContent())
                .pageNo(page)
                .pageSize(size)
                .totalElements(organizationOperators.getNumberOfElements())
                .totalPages(organizationOperators.getTotalPages())
                .build());
    }

    @Override
    public ResponseEntity<OperatorDTO> createOrganizationOperator(String organizationIpaCode,
        CreateOperatorRequest createOperatorRequest) {
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
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to delete operator with mappedExternalUserId " + mappedExternalUserId);
        }
        authzService.deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
        return ResponseEntity.ok(null);
    }
}
