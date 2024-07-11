package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OperatorsRepository extends OperatorsRepositoryExt, MongoRepository<Operator, String> {
    List<Operator> findAllByUserId(String userId);
    Optional<Operator> findByOperatorIdAndOrganizationIpaCode(String OperatorId, String organizationIpaCode);
    Page<Operator> findAllByOrganizationIpaCode(String organizationIpaCode, Pageable pageable);
}
