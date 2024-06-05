package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.UserInfo;

public interface TokenStoreService {
    UserInfo save(String accessToken, UserInfo userInfo);
    UserInfo load(String accessToken);
    void delete(String accessToken);
}
