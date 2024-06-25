package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.Operator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class OperatorRegistrationService {

    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles){
        log.info("Registering relationship between userId {} and organization {} setting roles {}",
                userId, organizationIpaCode, roles);

        return null; // TODO
    }
}
