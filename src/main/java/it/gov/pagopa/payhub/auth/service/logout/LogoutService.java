package it.gov.pagopa.payhub.auth.service.logout;

public interface LogoutService {
    void logout(String clientId, String token);
}
