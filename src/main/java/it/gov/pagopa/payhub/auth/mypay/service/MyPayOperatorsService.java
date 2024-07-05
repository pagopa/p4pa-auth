package it.gov.pagopa.payhub.auth.mypay.service;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyPayOperatorsService {

  private final MyPayOperatorsRepository myPayOperatorsRepository;

  public MyPayOperatorsService(MyPayOperatorsRepository myPayOperatorsRepository){
    this.myPayOperatorsRepository = myPayOperatorsRepository;
  }

  public void registerMyPayOperator(String mappedExternalUserId,String email, String organizationIpaCode,
      Set<String> roles) {
    //exit if doesn't exist either roles
    if(!roles.contains(Constants.ROLE_ADMIN) && !roles.contains(Constants.ROLE_OPER)) {
      log.info("Operator with mappedExternalUserId {} doesn't contain any admitted roles for MyPay", mappedExternalUserId);
      return;
    }

    Optional<MyPayOperator> existingMyPayOperator = myPayOperatorsRepository.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode);
    //if exist update else insert
    existingMyPayOperator.ifPresentOrElse(operator -> {
      operator.setRuolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null);
      operator.setDeEmailAddress(email);
      myPayOperatorsRepository.save(operator);
      log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is updated on MyPay ",
          mappedExternalUserId,organizationIpaCode, roles);
    }, () -> {
      myPayOperatorsRepository.save(MyPayOperator.builder()
        .ruolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null)
        .codFedUserId(mappedExternalUserId)
        .codIpaEnte(organizationIpaCode)
        .deEmailAddress(email)
        .build());
      log.info("Operator with mappedExternalUserId {}, organization {} and roles {} is saved on MyPay ",
          mappedExternalUserId,organizationIpaCode, roles);
    });
  }
}
