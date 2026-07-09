package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final TokenStoreService tokenStoreService;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final ValidateRefreshTokenService validateRefreshTokenService;

    public RefreshTokenServiceImpl(TokenStoreService tokenStoreService, AccessTokenBuilderService accessTokenBuilderService, ValidateRefreshTokenService validateRefreshTokenService) {
        this.tokenStoreService = tokenStoreService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.validateRefreshTokenService = validateRefreshTokenService;
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

        tokenStoreService.deleteRefreshToken(refreshToken);
        AccessToken newAccessToken = accessTokenBuilderService.build(userInfo);

        tokenStoreService.save(newAccessToken.getAccessToken(), userInfo);
        tokenStoreService.saveRefreshToken(newAccessToken.getRefreshToken(), userInfo);

        return newAccessToken;
    }
}
