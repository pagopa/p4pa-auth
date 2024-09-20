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

  public void registerMyPivotUser(String mappedExternalUserId) {
    Optional<MyPivotUser> existedUser = myPivotUsersRepository.findByCodFedUserId(mappedExternalUserId);
    existedUser.ifPresentOrElse(myPivotUsersRepository::save, () ->
      myPivotUsersRepository.save(MyPivotUser.builder()
          .codFedUserId(mappedExternalUserId)
          .build()));
  }
}
