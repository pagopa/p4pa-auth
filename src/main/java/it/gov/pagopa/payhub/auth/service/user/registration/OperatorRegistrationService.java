package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class OperatorRegistrationService {

    private final OperatorsRepository operatorsRepository;

    public OperatorRegistrationService(OperatorsRepository operatorsRepository) {
        this.operatorsRepository = operatorsRepository;
    }

    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles){
        log.info("Registering relationship between userId {} and organization {} setting roles {}",
                userId, organizationIpaCode, roles);

        return operatorsRepository.registerOperator(userId, organizationIpaCode, roles);
    }
}
