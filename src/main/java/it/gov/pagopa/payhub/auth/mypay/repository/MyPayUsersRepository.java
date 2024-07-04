package it.gov.pagopa.payhub.auth.mypay.repository;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("myPayUsersRepository")
public interface MyPayUsersRepository extends JpaRepository<MyPayUser, Long> {
  MyPayUser findByCodFedUserId(String userId);
}
