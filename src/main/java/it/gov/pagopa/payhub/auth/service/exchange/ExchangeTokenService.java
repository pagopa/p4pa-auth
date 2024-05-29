package it.gov.pagopa.payhub.auth.service.exchange;

public interface ExchangeTokenService {
    void postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope);
}
