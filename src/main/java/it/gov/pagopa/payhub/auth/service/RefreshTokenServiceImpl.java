package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;


@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final TokenStoreService tokenStoreService;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final ValidateRefreshTokenService validateRefreshTokenService;
    private final int maxLifetimeSeconds;

    public RefreshTokenServiceImpl(TokenStoreService tokenStoreService,
                                   AccessTokenBuilderService accessTokenBuilderService,
                                   ValidateRefreshTokenService validateRefreshTokenService,
                                   @Value("${jwt.refresh-token.max-lifetime-seconds}") int maxLifetimeSeconds) {
        this.tokenStoreService = tokenStoreService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.validateRefreshTokenService = validateRefreshTokenService;
        this.maxLifetimeSeconds = maxLifetimeSeconds;
    }

    @Override
    public AccessToken refreshToken(String clientId, String refreshToken) {
        log.info("Client {} requested a token refresh", clientId);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "Missing refresh_token parameter");
        }
        validateRefreshTokenService.validate(clientId, refreshToken);

        IamUserInfoDTO userInfo = tokenStoreService.loadRefreshToken(refreshToken);
        if (userInfo == null) {
            throw new InvalidAccessTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "RefreshToken not found");
        }

        long nowSeconds = Instant.now().getEpochSecond();
        if (userInfo.getIssueAt() == null) {
            userInfo.setIssueAt(nowSeconds);
        }

        long elapsedTime = nowSeconds - userInfo.getIssueAt();
        int remainingSessionLifetime = (int) (maxLifetimeSeconds - elapsedTime);

        if (remainingSessionLifetime <= 0) {
            log.warn("Max session lifetime reached for user {}. Invalidating session.", userInfo.getMappedExternalUserId());
            tokenStoreService.deleteRefreshToken(refreshToken);
            throw new InvalidAccessTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "Session expired, re-authentication required");
        }

        tokenStoreService.deleteRefreshToken(refreshToken);
        AccessToken newAccessToken = accessTokenBuilderService.build(userInfo, null, remainingSessionLifetime, true);

        tokenStoreService.save(newAccessToken.getAccessToken(), userInfo);
        tokenStoreService.saveRefreshToken(newAccessToken.getRefreshToken(), userInfo, remainingSessionLifetime);

        return newAccessToken;
    }
}
