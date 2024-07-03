package it.gov.pagopa.payhub.auth.mypay.repository;


import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("myPayOperatorsRepository")
public interface MyPayOperatorsRepository extends JpaRepository <MyPayOperator, Long> {
  MyPayOperator findByCodFedUserIdAndCodIpaEnte(String codFedUserId, String codIpaEnte);
}
