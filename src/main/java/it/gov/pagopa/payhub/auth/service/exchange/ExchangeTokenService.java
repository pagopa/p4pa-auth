package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.model.generated.AccessToken;

public interface ExchangeTokenService {
    AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope);
}
