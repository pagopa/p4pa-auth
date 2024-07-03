package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import it.gov.pagopa.payhub.auth.mypay.service.MyPayOperatorsService;
import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class OperatorRegistrationService {

    private final OperatorsRepository operatorsRepository;
    private final MyPayOperatorsService myPayOperatorsService;
    private final MyPivotOperatorsRepository myPivotOperatorsRepository;

    public OperatorRegistrationService(OperatorsRepository operatorsRepository,
        MyPayOperatorsService myPayOperatorsService,
        MyPivotOperatorsRepository myPivotOperatorsRepository) {
        this.operatorsRepository = operatorsRepository;
        this.myPivotOperatorsRepository = myPivotOperatorsRepository;
        this.myPayOperatorsService = myPayOperatorsService;
    }

    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles, String mappedExternalUserId, String email){
        log.info("Registering relationship between userId {} and organization {} setting roles {}",
                userId, organizationIpaCode, roles);

        this.registerMyPivotOperator(mappedExternalUserId, organizationIpaCode, roles);

        myPayOperatorsService.registerMyPayOperator(mappedExternalUserId, email, organizationIpaCode, roles);
        log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is saved on MyPay ",
            mappedExternalUserId,organizationIpaCode, roles);

        return operatorsRepository.registerOperator(userId, organizationIpaCode, roles);
    }

    private void registerMyPivotOperator(String mappedExternalUserId, String organizationIpaCode, Set<String> roles) {
        Optional<MyPivotOperator> existingMyPivotOperator = Optional.ofNullable(
            myPivotOperatorsRepository.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId,
                organizationIpaCode));
        //if exist update else insert
        if(existingMyPivotOperator.isPresent()) {
            MyPivotOperator operator = existingMyPivotOperator.get();
            operator.setRuolo(roles.stream().findFirst().orElseThrow());
            myPivotOperatorsRepository.save(operator);
        } else {
            myPivotOperatorsRepository.save(MyPivotOperator.builder()
                .codFedUserId(mappedExternalUserId)
                .ruolo(roles.stream().findFirst().orElseThrow())
                .codIpaEnte(organizationIpaCode)
                .build());
        }
        log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is registered on MyPivot",
            mappedExternalUserId,organizationIpaCode, roles);
    }
}
