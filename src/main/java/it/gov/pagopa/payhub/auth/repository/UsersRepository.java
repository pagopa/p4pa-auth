package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends UsersRepositoryExt, MongoRepository<String, User> {
}
