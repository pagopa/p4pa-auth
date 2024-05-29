package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.AccessToken;

public interface AuthService {

    AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope);
}
