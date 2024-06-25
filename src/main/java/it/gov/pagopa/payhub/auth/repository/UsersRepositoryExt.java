package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.User;

public interface UsersRepositoryExt {
    User registerUser(User user);
}
