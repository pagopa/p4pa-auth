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

  public void registerMyPayUser(String mappedExternalUserId, String fiscalCode, String firstName, String lastName, String email) {
      Optional<MyPayUser> existedUser = myPayUsersRepository.findByCodFedUserId(mappedExternalUserId);
      existedUser.ifPresentOrElse(myPayUser -> {
        myPayUser.setDeEmailAddress(email);
        myPayUser.setDeFirstname(firstName);
        myPayUser.setDeLastname(lastName);
        myPayUser.setDtUltimoLogin(new Date());
        myPayUsersRepository.save(myPayUser);
    }, () -> myPayUsersRepository.save(MyPayUser.builder()
          .version(0)
          .codFedUserId(mappedExternalUserId)
          .codCodiceFiscaleUtente(fiscalCode)
          .flgFedAuthorized(false)
          .deEmailAddress(email)
          .deFirstname(firstName)
          .deLastname(lastName)
          .deFedLegalEntity("fisica")
          .dtUltimoLogin(new Date())
          .emailSourceType('A')
          .build()));
  }
}
