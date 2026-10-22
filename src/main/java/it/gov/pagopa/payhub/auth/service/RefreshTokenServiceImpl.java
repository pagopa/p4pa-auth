package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.AuthenticatedIamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.auth.utils.Utilities;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final TokenStoreService tokenStoreService;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final ValidateRefreshTokenService validateRefreshTokenService;
    private final int maxSessionLifetimeSeconds;

    public RefreshTokenServiceImpl(TokenStoreService tokenStoreService,
                                   AccessTokenBuilderService accessTokenBuilderService,
                                   ValidateRefreshTokenService validateRefreshTokenService,
                                   @Value("${jwt.max-session-lifetime-seconds}") int maxSessionLifetimeSeconds) {
        this.tokenStoreService = tokenStoreService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.validateRefreshTokenService = validateRefreshTokenService;
        this.maxSessionLifetimeSeconds = maxSessionLifetimeSeconds;
    }

    @Override
    public AccessToken refreshToken(String clientId, String refreshToken) {
        log.info("Client {} requested a token refresh", clientId);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "Missing refresh_token parameter");
        }
        validateRefreshTokenService.validate(clientId, refreshToken);

        AuthenticatedIamUserInfoDTO userInfo = tokenStoreService.loadRefreshToken(refreshToken);
        if (userInfo == null) {
            throw new InvalidAccessTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "RefreshToken not found");
        }

        long nowSeconds = Utilities.nowInSeconds();
        if (userInfo.getIamUserInfo().getIssuedAt() == null) {
            userInfo.getIamUserInfo().setIssuedAt(nowSeconds);
        }

        long elapsedTime = nowSeconds - userInfo.getIamUserInfo().getIssuedAt();
        int remainingSessionLifetime = (int) (maxSessionLifetimeSeconds - elapsedTime);

        if (remainingSessionLifetime <= 0) {
            log.warn("Max session lifetime reached for user {}. Invalidating session.", userInfo.getIamUserInfo().getMappedExternalUserId());
            tokenStoreService.deleteRefreshToken(refreshToken);
            throw new InvalidAccessTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "Session expired, re-authentication required");
        }

        tokenStoreService.deleteRefreshToken(refreshToken);
        AccessToken newAccessToken = accessTokenBuilderService.build(userInfo.getIamUserInfo(), null, remainingSessionLifetime, true);

        AuthenticatedIamUserInfoDTO authenticatedIamUserInfoDTO = new AuthenticatedIamUserInfoDTO();
        authenticatedIamUserInfoDTO.setAccessToken(newAccessToken.getAccessToken());
        authenticatedIamUserInfoDTO.setIamUserInfo(userInfo.getIamUserInfo());

        tokenStoreService.save(newAccessToken.getAccessToken(), userInfo.getIamUserInfo());
        tokenStoreService.saveRefreshToken(newAccessToken.getRefreshToken(), authenticatedIamUserInfoDTO, remainingSessionLifetime);

        return newAccessToken;
    }
}
