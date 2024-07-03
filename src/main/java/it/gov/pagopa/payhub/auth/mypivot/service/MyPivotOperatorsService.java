package it.gov.pagopa.payhub.auth.mypivot.service;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPivotOperatorsService {

  @Autowired
  private MyPivotOperatorsRepository myPivotOperatorsRepository;

  public void registerMyPivotOperator(String mappedExternalUserId, String organizationIpaCode, Set<String> roles) {
    Optional<MyPivotOperator> existingMyPivotOperator = Optional.ofNullable(
        myPivotOperatorsRepository.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId,
            organizationIpaCode));
    //if exist update else insert
    if(existingMyPivotOperator.isPresent()) {
      MyPivotOperator operator = existingMyPivotOperator.get();
      operator.setRuolo(roles.stream().findFirst().orElse(null));
      myPivotOperatorsRepository.save(operator);
    } else {
      myPivotOperatorsRepository.save(MyPivotOperator.builder()
          .codFedUserId(mappedExternalUserId)
          .ruolo(roles.stream().findFirst().orElse(null))
          .codIpaEnte(organizationIpaCode)
          .build());
    }
  }
}
