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

  public MyPivotUser registerMyPivotUser(String externalUserId, String fiscalCode, String firstName, String lastName, String email) {
    Optional<MyPivotUser> existedUser = Optional.ofNullable(myPivotUsersRepository.findByCodFedUserId(externalUserId));
    if(existedUser.isPresent()){
      MyPivotUser myPivotUser = existedUser.get();
      myPivotUser.setDeEmailAddress(email);
      myPivotUser.setDeFirstname(firstName);
      myPivotUser.setDeLastname(lastName);
      return myPivotUsersRepository.save(myPivotUser);
    }else {
      return myPivotUsersRepository.save(MyPivotUser.builder()
          .version(0)
          .codFedUserId(externalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .deEmailAddress(email)
          .deFirstname(firstName)
          .deLastname(lastName)
          .build());
    }
  }
}
