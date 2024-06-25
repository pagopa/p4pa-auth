package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserInfo;

import java.util.Set;

public interface UserService {
    User registerUser(String externalUserId, String fiscalCode, String iamIssuer);
    Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles);
    UserInfo getUserInfo(String accessToken);
}
