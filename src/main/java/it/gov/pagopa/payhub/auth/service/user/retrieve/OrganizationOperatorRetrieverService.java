package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class OrganizationOperatorRetrieverService {

    private final OperatorsRepository operatorsRepository;
    private final UsersRepository usersRepository;
    private final OperatorDTOMapper operatorDTOMapper;

    public OrganizationOperatorRetrieverService(OperatorsRepository operatorsRepository, UsersRepository usersRepository, OperatorDTOMapper operatorDTOMapper) {
        this.operatorsRepository = operatorsRepository;
        this.usersRepository = usersRepository;
        this.operatorDTOMapper = operatorDTOMapper;
    }

    public Page<OperatorDTO> retrieveOrganizationOperators(String organizationIpaCode, Pageable pageable) {
        Page<Operator> operators = operatorsRepository.findAllByOrganizationIpaCode(organizationIpaCode, pageable);
        return new PageImpl<>(
                operators.stream()
                .map(op -> {
                    Optional<User> user = usersRepository.findById(op.getUserId());
                    if(user.isEmpty()){
                        log.warn("Found an operator without a user: {}", op);
                        return null;
                    } else {
                        return operatorDTOMapper.apply(user.get(), op);
                    }
                })
                .filter(Objects::nonNull)
                .toList(),
                pageable,
                operators.getTotalElements());
    }
}
