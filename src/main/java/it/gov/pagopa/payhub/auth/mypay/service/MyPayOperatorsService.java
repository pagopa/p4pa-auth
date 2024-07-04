package it.gov.pagopa.payhub.auth.mypay.service;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MyPayOperatorsService {

  private final MyPayOperatorsRepository myPayOperatorsRepository;

  public MyPayOperatorsService(MyPayOperatorsRepository myPayOperatorsRepository){
    this.myPayOperatorsRepository = myPayOperatorsRepository;
  }

  public void registerMyPayOperator(String mappedExternalUserId,String email, String organizationIpaCode,
      Set<String> roles) {

    Optional<MyPayOperator> existingMyPayOperator = Optional.ofNullable(
        myPayOperatorsRepository.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode));
    //if exist update else insert
    if(existingMyPayOperator.isPresent()) {
      MyPayOperator operator = existingMyPayOperator.get();
      operator.setRuolo(roles.stream().findFirst().orElse(null));
      operator.setDeEmailAddress(email);
      myPayOperatorsRepository.save(operator);
    } else {
      myPayOperatorsRepository.save(MyPayOperator.builder()
        .ruolo(roles.stream().findFirst().orElse(null))
        .codFedUserId(mappedExternalUserId)
        .codIpaEnte(organizationIpaCode)
        .deEmailAddress(email)
        .build());
    }
  }
}
