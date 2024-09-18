package it.gov.pagopa.payhub.auth.mypay.service;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayUsersRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MyPayUsersService {

  private final MyPayUsersRepository myPayUsersRepository;

  public MyPayUsersService(MyPayUsersRepository myPayUsersRepository) {
    this.myPayUsersRepository = myPayUsersRepository;
  }

  public void registerMyPayUser(String mappedExternalUserId) {
      Optional<MyPayUser> existedUser = myPayUsersRepository.findByCodFedUserId(mappedExternalUserId);
      existedUser.ifPresentOrElse(myPayUsersRepository::save, () -> myPayUsersRepository.save(MyPayUser.builder()
          .codFedUserId(mappedExternalUserId)
          .build()));
  }
}
