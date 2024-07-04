package it.gov.pagopa.payhub.auth.mypay.service;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayUsersRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPayUsersService {

  @Autowired
  private MyPayUsersRepository myPayUsersRepository;

  public void registerMyPayUser(String externalUserId, String fiscalCode, String firstName, String lastName, String email) {
    Optional<MyPayUser> existedUser = Optional.ofNullable(myPayUsersRepository.findByCodFedUserId(externalUserId));
    if(existedUser.isPresent()){
      MyPayUser myPayUser = existedUser.get();
      myPayUser.setDeEmailAddress(email);
      myPayUser.setDeFirstname(firstName);
      myPayUser.setDeLastname(lastName);
      //myPayUser.setIndirizzo("via pluto");
      //myPayUser.setCivico("9");
      //myPayUser.setCap("CAP");
      //myPayUser.setComuneId((long)1);
      //myPayUser.setProvinciaId((long) 1);
      //myPayUser.setNazioneId((long) 1);
      //myPayUser.setDeEmailAddressNew("yy@yy");
      myPayUsersRepository.save(myPayUser);
    }else {
      myPayUsersRepository.save(MyPayUser.builder()
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
