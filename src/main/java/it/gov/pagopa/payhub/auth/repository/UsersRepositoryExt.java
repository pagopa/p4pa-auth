package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersRepositoryExt {
    User registerUser(User user);
    Page<User> retrieveUsers(String fiscalCode, String firstName, String lastName, Pageable pageable);
}
