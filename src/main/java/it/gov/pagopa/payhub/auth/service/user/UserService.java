package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserInfo;

public interface UserService {
    User registerUser(String externalUserId, String fiscalCode, String iamIssuer);
    UserInfo getUserInfo(String accessToken);
}
