package it.gov.pagopa.payhub.auth.mypay.repository;


import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("myPayOperatorsRepository")
public interface MyPayOperatorsRepository extends JpaRepository <MyPayOperator, Long> {
  Optional<MyPayOperator> findByCodFedUserIdAndCodIpaEnte(String codFedUserId, String codIpaEnte);
  void deleteByCodIpaEnteAndCodFedUserId(String codIpaEnte, String codFedUserId);
}
