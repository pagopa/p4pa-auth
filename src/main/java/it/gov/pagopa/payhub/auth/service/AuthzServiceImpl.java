package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthzServiceImpl implements AuthzService {

    private final UserService userService;
    private final OperatorsRepository operatorsRepository;

    public AuthzServiceImpl(UserService userService, OperatorsRepository operatorsRepository) {
        this.userService = userService;
        this.operatorsRepository = operatorsRepository;
    }

    @Override
    public Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest) {
        return userService.retrieveOrganizationOperators(organizationIpaCode, pageRequest);
    }

    @Override
    @Transactional
    public void deleteOrganizationOperator(String organizationIpaCode, String operatorId) {
        operatorsRepository.deleteByOrganizationIpaCodeAndOperatorId(organizationIpaCode, operatorId);
    }
}
