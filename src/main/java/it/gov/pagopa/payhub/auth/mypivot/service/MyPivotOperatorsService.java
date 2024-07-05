package it.gov.pagopa.payhub.auth.mypivot.service;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyPivotOperatorsService {

  private final MyPivotOperatorsRepository myPivotOperatorsRepository;

  public MyPivotOperatorsService(MyPivotOperatorsRepository myPivotOperatorsRepository) {
    this.myPivotOperatorsRepository = myPivotOperatorsRepository;
  }

  public void registerMyPivotOperator(String mappedExternalUserId, String organizationIpaCode, Set<String> roles) {
    //exit if doesn't exist either roles
    if(!roles.contains(Constants.ROLE_ADMIN) && !roles.contains(Constants.ROLE_OPER)) {
      log.info("Operator with mappedExternalUserId {} doesn't contain any admitted roles for MyPay", mappedExternalUserId);
      return;
    }

    Optional<MyPivotOperator> existingMyPivotOperator = myPivotOperatorsRepository.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId,
        organizationIpaCode);
    //if exist update else insert
    existingMyPivotOperator.ifPresentOrElse(operator -> {
      operator.setRuolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null);
      myPivotOperatorsRepository.save(operator);
      log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is updated on MyPivot ",
          mappedExternalUserId,organizationIpaCode, roles);
    }, () -> {
      myPivotOperatorsRepository.save(MyPivotOperator.builder()
          .codFedUserId(mappedExternalUserId)
          .ruolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null)
          .codIpaEnte(organizationIpaCode)
          .build());
      log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is saved on MyPivot ",
          mappedExternalUserId,organizationIpaCode, roles);
    });
  }
}
