package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.interfaces.Claim;
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

@Service
@Slf4j
public class ValidateRefreshTokenService {
    public static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final String allowedIssuer;
    private final String allowedAudience;
    private final String urlJwkProvider;
    private final JWTValidator jwtValidator;

    public ValidateRefreshTokenService(
            @Value("${jwt.external-token.issuer:}") String allowedIssuer,
            @Value("${jwt.audience}") String allowedAudience,
            @Value("${jwt.external-token.base-url:}") String urlJwkProvider,
            JWTValidator jwtValidator) {
        this.allowedIssuer = allowedIssuer;
        this.allowedAudience = allowedAudience;
        this.urlJwkProvider = urlJwkProvider;
        this.jwtValidator = jwtValidator;
    }

    public Map<String, Claim> validate(String clientId, String refreshToken) {
        validateClient(clientId);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "refresh_token is mandatory");
        }

        Map<String, Claim> claims = jwtValidator.validate(refreshToken, urlJwkProvider);
        if (!allowedIssuer.equals(claims.get(Claims.ISSUER).asString())) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_ISSUER, "Invalid refresh token issuer");
        }
        if (!allowedAudience.equals(claims.get(Claims.AUDIENCE).asString())) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_AUDIENCE, "Invalid refresh token audience");
        }
        if (!GRANT_TYPE_REFRESH_TOKEN.equals(claims.get("typ").asString())) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN_TYPE, "Token is not a refresh token");
        }

        log.info("Refresh token cryptographic validation passed");
        return claims;
    }

    private void validateClient(String clientId) {
        if (!ALLOWED_CLIENT_ID.equals(clientId)) {
            throw new InvalidExchangeClientException(ErrorCodeConstants.ERROR_CODE_INVALID_CLIENT_ID, "Invalid clientId " + clientId);
        }
    }
}
