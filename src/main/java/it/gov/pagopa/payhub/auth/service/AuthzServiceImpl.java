package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.exception.custom.OperatorNotFoundException;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OperatorDTOMapper;
import it.gov.pagopa.payhub.auth.service.user.retrieve.UserDTOMapper;
import it.gov.pagopa.payhub.model.generated.CreateOperatorRequest;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.UserDTO;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthzServiceImpl implements AuthzService {

    private final UserService userService;
    private final UsersRepository usersRepository;
    private final OperatorsRepository operatorsRepository;
    private final OperatorDTOMapper operatorDTOMapper;
    private final UserDTOMapper userDTOMapper;
    private static final String MYPAYIAMISSUERS = "MYPAY";

    public AuthzServiceImpl(UserService userService, UsersRepository usersRepository,
        OperatorsRepository operatorsRepository, OperatorDTOMapper operatorDTOMapper, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.usersRepository = usersRepository;
        this.operatorsRepository = operatorsRepository;
        this.operatorDTOMapper = operatorDTOMapper;
        this.userDTOMapper = userDTOMapper;
    }

    @Override
    public Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest) {
        return userService.retrieveOrganizationOperators(organizationIpaCode, pageRequest);
    }

    @Override
    public Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, String fiscalCode,
        String firstName, String lastName, Pageable pageRequest) {
        Page<User> users = usersRepository.retrieveUsers(fiscalCode, firstName, lastName, pageRequest);
       return new PageImpl<>(users.stream().map(user -> {
            Optional<Operator> operator = operatorsRepository.findById(user.getUserId()+organizationIpaCode);
         return operator.map(value -> operatorDTOMapper.apply(user, value)).orElse(null);
       }).filter(Objects::nonNull).toList(),
           pageRequest,
           users.getTotalElements());
    }

    @Override
    public OperatorDTO getOrganizationOperator(String organizationIpaCode,
        String mappedExternalUserId) {
        User user = usersRepository.findByMappedExternalUserId(mappedExternalUserId)
            .orElseThrow(() -> new UserNotFoundException("User with this mappedExternalUserId not found"));
        Operator operator = operatorsRepository.findById(user.getUserId()+organizationIpaCode)
            .orElseThrow(() -> new OperatorNotFoundException("Operator with this userId "+ user.getUserId()+organizationIpaCode + "not found"));
        return operatorDTOMapper.apply(user,operator);
    }

    @Override
    @Transactional
    public void deleteOrganizationOperator(String organizationIpaCode, String mappedExternalUserId) {
        operatorsRepository.deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
    }

    @Override
    public OperatorDTO createOrganizationOperator(String organizationIpaCode, CreateOperatorRequest createOperatorRequest) {
        User user = userService.registerUser(createOperatorRequest.getExternalUserId(), createOperatorRequest.getFiscalCode(),
            MYPAYIAMISSUERS, createOperatorRequest.getFirstName(), createOperatorRequest.getLastName(), createOperatorRequest.getEmail());
        Operator operator = userService.registerOperator(user.getUserId(), organizationIpaCode, new HashSet<>(createOperatorRequest.getRoles()), createOperatorRequest.getExternalUserId(), user.getEmail());
        return operatorDTOMapper.apply(user,operator);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = userService.registerUser(userDTO.getExternalUserId(), userDTO.getFiscalCode(), MYPAYIAMISSUERS
            , userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        return userDTOMapper.map(user);
    }
}
