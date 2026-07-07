package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

import static it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService.REFRESH_TOKEN_TYPE;

@Service
@Slf4j
public class ValidateRefreshTokenService {
    public static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final String allowedAudience;

    public ValidateRefreshTokenService(@Value("${jwt.audience}") String allowedAudience) {
        this.allowedAudience = allowedAudience;
    }

    public Map<String, Claim> validate(String clientId, String refreshToken) {
        validateClient(clientId);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "refresh_token is mandatory");
        }

        try {
            DecodedJWT jwt = JWT.decode(refreshToken);
            Map<String, Claim> claims = jwt.getClaims();

            if (!allowedAudience.equals(claims.get(Claims.ISSUER).asString())) {
                throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_ISSUER, "Invalid refresh token issuer");
            }

            if (!REFRESH_TOKEN_TYPE.equals(claims.get("typ").asString())) {
                throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN_TYPE, "Token is not a refresh token");
            }

            log.info("Refresh token structure and claims validation passed");
            return claims;

        } catch (com.auth0.jwt.exceptions.TokenExpiredException e){
            throw new TokenExpiredException(e.getMessage());
        } catch (JWTDecodeException e) {
            throw new InvalidTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "The refresh token is malformed", e);
        }
    }

    private void validateClient(String clientId) {
        if (!ALLOWED_CLIENT_ID.equals(clientId)) {
            throw new InvalidExchangeClientException(ErrorCodeConstants.ERROR_CODE_INVALID_CLIENT_ID, "Invalid clientId " + clientId);
        }
    }
}
