package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OperatorDTOMapper;
import it.gov.pagopa.payhub.model.generated.CreateOperatorRequest;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthzServiceImpl implements AuthzService {

    private final UserService userService;
    private final OperatorsRepository operatorsRepository;
    private final OperatorDTOMapper operatorDTOMapper;

    public AuthzServiceImpl(UserService userService, OperatorsRepository operatorsRepository,
        OperatorDTOMapper operatorDTOMapper) {
        this.userService = userService;
        this.operatorsRepository = operatorsRepository;
        this.operatorDTOMapper = operatorDTOMapper;
    }

    @Override
    public Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest) {
        return userService.retrieveOrganizationOperators(organizationIpaCode, pageRequest);
    }

    @Override
    @Transactional
    public void deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId) {
        operatorsRepository.deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
    }

    @Override
    public OperatorDTO createOrganizationOperator(String organizationIpaCode, CreateOperatorRequest createOperatorRequest) {
        User user = userService.registerUser(createOperatorRequest.getExternalUserId(), createOperatorRequest.getFiscalCode(),
            "MYPAY", createOperatorRequest.getFirstName(), createOperatorRequest.getLastName(), createOperatorRequest.getEmail());
        Operator operator = userService.registerOperator(user.getUserId(), organizationIpaCode, new HashSet<>(createOperatorRequest.getRoles()), createOperatorRequest.getExternalUserId(), user.getEmail());
        return operatorDTOMapper.apply(user,operator);
    }
}
