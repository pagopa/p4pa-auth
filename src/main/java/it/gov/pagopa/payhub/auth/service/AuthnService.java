package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.UserInfo;

public interface AuthnService {

    AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret);
    UserInfo getUserInfo(String accessToken);
    void logout(String clientId, String token);

}
