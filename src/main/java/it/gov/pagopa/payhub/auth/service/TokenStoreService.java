package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.AuthenticatedIamUserInfoDTO;

public interface TokenStoreService {
    IamUserInfoDTO save(String accessToken, IamUserInfoDTO userInfo);
    IamUserInfoDTO load(String accessToken);
    void delete(String accessToken);

    AuthenticatedIamUserInfoDTO saveRefreshToken(String refreshToken, AuthenticatedIamUserInfoDTO idTokenClaims);
    AuthenticatedIamUserInfoDTO saveRefreshToken(String refreshToken, AuthenticatedIamUserInfoDTO idTokenClaims, long ttlInSeconds);
    AuthenticatedIamUserInfoDTO loadRefreshToken(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
