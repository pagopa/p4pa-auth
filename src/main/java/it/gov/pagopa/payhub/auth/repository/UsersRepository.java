package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsersRepository extends UsersRepositoryExt, MongoRepository<User, String> {
    Optional<User> findByMappedExternalUserId(String mappedExternalUserId);
    Page<User> findByFiscalCodeIgnoreCase(String fiscalCode, Pageable pageable);
}
