package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsersRepository extends UsersRepositoryExt, MongoRepository<User, String> {
    Optional<User> findByMappedExternalUserId(String mappedExternalUserId);
    Optional<User> findByUserId(String userId);
}
