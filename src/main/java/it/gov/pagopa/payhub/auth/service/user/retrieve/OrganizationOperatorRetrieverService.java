package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<OperatorDTO> retrieveOrganizationOperators(String organizationIpaCode) {
        List<Operator> operators = operatorsRepository.findAllByOrganizationIpaCode(organizationIpaCode);
        return operators.stream()
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
                .toList();
    }
}
