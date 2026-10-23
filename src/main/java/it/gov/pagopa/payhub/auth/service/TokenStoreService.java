package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.RefreshTokenSessionDTO;

public interface TokenStoreService {
    IamUserInfoDTO save(String accessToken, IamUserInfoDTO userInfo);
    IamUserInfoDTO load(String accessToken);
    void delete(String accessToken);

    RefreshTokenSessionDTO saveRefreshToken(String refreshToken, RefreshTokenSessionDTO refreshTokenSession);
    RefreshTokenSessionDTO saveRefreshToken(String refreshToken, RefreshTokenSessionDTO refreshTokenSession, long ttlInSeconds);
    RefreshTokenSessionDTO loadRefreshToken(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
