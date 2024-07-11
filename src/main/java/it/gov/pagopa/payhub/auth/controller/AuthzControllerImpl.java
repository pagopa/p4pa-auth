package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.controller.generated.AuthzApi;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.OperatorsPage;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthzControllerImpl implements AuthzApi {

    private final AuthzService authzService;
    private final OperatorsRepository operatorsRepository;
    private final UsersRepository userRepository;
    public AuthzControllerImpl(AuthzService authzService, OperatorsRepository operatorsRepository, UsersRepository usersRepository) {
        this.authzService = authzService;
        this.operatorsRepository = operatorsRepository;
        this.userRepository = usersRepository;
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
    public ResponseEntity<Void> deleteOrganizationOperator(String organizationIpaCode, String operatorId) {
        if(!SecurityUtils.isPrincipalAdmin(organizationIpaCode)){
            throw new UserUnauthorizedException("User not allowed to delete operator " + operatorId);
        }
        Optional<Operator> operator = operatorsRepository.findByOperatorIdAndOrganizationIpaCode(operatorId, organizationIpaCode);
        operator.ifPresent(operatorFound -> {
            Optional<User> user = userRepository.findByUserId(operatorFound.getUserId());
            if(user.isPresent())
                throw new UserNotFoundException("Can't delete operator with operatorId "+operatorId+" and organizationIpaCode "+organizationIpaCode+" because exists linked user");
            operatorsRepository.delete(operatorFound);
        });

        return ResponseEntity.ok(null);
    }
}
