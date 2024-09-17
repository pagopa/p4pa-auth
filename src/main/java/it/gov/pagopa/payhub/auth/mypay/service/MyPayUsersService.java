package it.gov.pagopa.payhub.auth.mypay.service;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayUsersRepository;
import java.util.Date;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MyPayUsersService {

  private final MyPayUsersRepository myPayUsersRepository;

  public MyPayUsersService(MyPayUsersRepository myPayUsersRepository) {
    this.myPayUsersRepository = myPayUsersRepository;
  }

  public void registerMyPayUser(String mappedExternalUserId, String fiscalCode, String firstName, String lastName) {
      Optional<MyPayUser> existedUser = myPayUsersRepository.findByCodFedUserId(mappedExternalUserId);
      existedUser.ifPresentOrElse(myPayUser -> {
        myPayUser.setDeFirstname(firstName);
        myPayUser.setDeLastname(lastName);
        myPayUser.setDtUltimoLogin(new Date());
        myPayUsersRepository.save(myPayUser);
    }, () -> myPayUsersRepository.save(MyPayUser.builder()
          .codFedUserId(mappedExternalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .deFirstname(firstName)
          .deLastname(lastName)
          .deFedLegalEntity("fisica")
          .dtUltimoLogin(new Date())
          .build()));
  }
}
