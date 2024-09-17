package it.gov.pagopa.payhub.auth.mypivot.service;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotUser;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotUsersRepository;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MyPivotUsersService {

  private final MyPivotUsersRepository myPivotUsersRepository;

  public MyPivotUsersService(MyPivotUsersRepository myPivotUsersRepository){
    this.myPivotUsersRepository = myPivotUsersRepository;
  }

  public void registerMyPivotUser(String mappedExternalUserId, String fiscalCode, String firstName, String lastName) {
    Optional<MyPivotUser> existedUser = myPivotUsersRepository.findByCodFedUserId(mappedExternalUserId);
    existedUser.ifPresentOrElse(myPivotUser -> {
      myPivotUser.setDeFirstname(firstName);
      myPivotUser.setDeLastname(lastName);
      myPivotUser.setDtUltimoLogin(new Date());
      myPivotUsersRepository.save(myPivotUser);
    }, () ->
      myPivotUsersRepository.save(MyPivotUser.builder()
          .codFedUserId(mappedExternalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .deFirstname(firstName)
          .deLastname(lastName)
          .dtUltimoLogin(new Date())
          .build()));
  }
}
