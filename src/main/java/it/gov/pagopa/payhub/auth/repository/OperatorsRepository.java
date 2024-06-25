package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperatorsRepository extends MongoRepository<Operator, String> {
}
