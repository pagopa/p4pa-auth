package it.gov.pagopa.payhub.auth.mypivot.service;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotUser;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotUsersRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MyPivotUsersService {

  private final MyPivotUsersRepository myPivotUsersRepository;

  public MyPivotUsersService(MyPivotUsersRepository myPivotUsersRepository){
    this.myPivotUsersRepository = myPivotUsersRepository;
  }

  public void registerMyPivotUser(String externalUserId, String fiscalCode, String firstName, String lastName, String email) {
    Optional<MyPivotUser> existedUser = myPivotUsersRepository.findByCodFedUserId(externalUserId);
    existedUser.ifPresentOrElse(myPivotUser -> {
      myPivotUser.setDeEmailAddress(email);
      myPivotUser.setDeFirstname(firstName);
      myPivotUser.setDeLastname(lastName);
      myPivotUsersRepository.save(myPivotUser);
    }, () ->
      myPivotUsersRepository.save(MyPivotUser.builder()
          .version(0)
          .codFedUserId(externalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .deEmailAddress(email)
          .deFirstname(firstName)
          .deLastname(lastName)
          .build()));
  }
}
