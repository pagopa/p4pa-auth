package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;

public interface AuthnService {

    AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType);
    UserInfo getUserInfo(String accessToken);
    void logout(String clientId, String token);

}
