package it.gov.pagopa.payhub.auth.mypivot.repository;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("myPivotUsersRepository")
public interface MyPivotUsersRepository extends JpaRepository<MyPivotUser,Long> {
  MyPivotUser findByCodFedUserId(String userId);
}
