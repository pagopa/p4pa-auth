package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

import static it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService.REFRESH_TOKEN_TYPE;
import static it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService.ALLOWED_CLIENT_ID;

@Service
@Slf4j
public class ValidateRefreshTokenService {
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final String allowedAudience;
    private final JWTValidator jwtValidator;

    public ValidateRefreshTokenService(@Value("${jwt.audience}") String allowedAudience, JWTValidator jwtValidator) {
        this.allowedAudience = allowedAudience;
        this.jwtValidator = jwtValidator;
    }

    public Map<String, Claim> validate(String clientId, String refreshToken) {
        validateClient(clientId);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "refresh_token is mandatory");
        }

        jwtValidator.validateInternalToken(refreshToken);
        DecodedJWT jwt = JWT.decode(refreshToken);
        Map<String, Claim> claims = jwt.getClaims();

        if (!allowedAudience.equals(claims.get(Claims.ISSUER).asString())) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_ISSUER, "Invalid refresh token issuer");
        }

        if (!REFRESH_TOKEN_TYPE.equals(claims.get("typ").asString())) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN_TYPE, "Token is not a refresh token");
        }
        return claims;
    }

    private void validateClient(String clientId) {
        if (!ALLOWED_CLIENT_ID.equals(clientId)) {
            throw new InvalidExchangeClientException(ErrorCodeConstants.ERROR_CODE_INVALID_CLIENT_ID, "Invalid clientId " + clientId);
        }
    }
}
