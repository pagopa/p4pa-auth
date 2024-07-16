package it.gov.pagopa.payhub.auth.mypivot.repository;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("myPivotOperatorsRepository")
public interface MyPivotOperatorsRepository extends JpaRepository<MyPivotOperator, Long> {
  Optional<MyPivotOperator> findByCodFedUserIdAndCodIpaEnte(String codFedUserId, String codIpaEnte);
  void deleteByCodIpaEnteAndCodFedUserId(String codIpaEnte, String codFedUserId);
}
