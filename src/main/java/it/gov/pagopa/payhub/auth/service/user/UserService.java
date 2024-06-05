package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.model.generated.UserInfo;

public interface UserService {
    UserInfo getUserInfo(String accessToken);
}
