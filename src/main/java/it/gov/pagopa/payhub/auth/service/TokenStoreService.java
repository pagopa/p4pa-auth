package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;

public interface TokenStoreService {
    IamUserInfoDTO save(String accessToken, IamUserInfoDTO userInfo);
    IamUserInfoDTO load(String accessToken);
    void delete(String accessToken);
}
