package it.gov.pagopa.payhub.auth.service;

public interface AuthService {

    void postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope);
}
