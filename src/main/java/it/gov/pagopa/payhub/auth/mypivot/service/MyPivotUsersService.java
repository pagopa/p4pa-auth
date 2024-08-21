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

  public void registerMyPivotUser(String mappedExternalUserId, String fiscalCode, String firstName, String lastName, String email) {
    Optional<MyPivotUser> existedUser = myPivotUsersRepository.findByCodFedUserId(mappedExternalUserId);
    existedUser.ifPresentOrElse(myPivotUser -> {
      myPivotUser.setDeEmailAddress(email);
      myPivotUser.setDeFirstname(firstName);
      myPivotUser.setDeLastname(lastName);
      myPivotUser.setDtUltimoLogin(new Date());
      myPivotUsersRepository.save(myPivotUser);
    }, () ->
      myPivotUsersRepository.save(MyPivotUser.builder()
          .version(0)
          .codFedUserId(mappedExternalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .deEmailAddress(email)
          .deFirstname(firstName)
          .deLastname(lastName)
          .emailSourceType('A')
          .dtUltimoLogin(new Date())
          .build()));
  }
}
