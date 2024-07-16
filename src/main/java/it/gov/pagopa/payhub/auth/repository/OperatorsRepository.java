package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OperatorsRepository extends OperatorsRepositoryExt, MongoRepository<Operator, String> {
    List<Operator> findAllByUserId(String userId);
    Page<Operator> findAllByOrganizationIpaCode(String organizationIpaCode, Pageable pageable);
    void deleteByOrganizationIpaCodeAndOperatorId( String organizationIpaCode, String operatorId);
}
