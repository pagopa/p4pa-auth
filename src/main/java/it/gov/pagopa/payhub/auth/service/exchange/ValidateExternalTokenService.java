package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@Slf4j
public class ValidateExternalTokenService {
    public static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    public static final String ALLOWED_GRANT_TYPE="urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String ALLOWED_SUBJECT_TOKEN_TYPE="urn:ietf:params:oauth:token-type:jwt";
    public static final String ALLOWED_SCOPE="openid";

    private final String allowedAudience;
    private final String allowedIssuer;
    private final String urlJwkProvider;
    private final JWTValidator jwtValidator;

    public ValidateExternalTokenService(@Value("${jwt.audience:}")String allowedAudience,
                                        @Value("${jwt.external-token.issuer:}")String allowedIssuer,
                                        @Value("${jwt.external-token.base-url:}")String urlJwkProvider,
                                        JWTValidator jwtValidator) {
        this.allowedAudience = allowedAudience;
        this.allowedIssuer = allowedIssuer;

        this.urlJwkProvider = urlJwkProvider;
        this.jwtValidator = jwtValidator;
    }

    public Map<String, Claim> validate(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        validateClient(clientId);
        validateProtocolConfiguration(grantType, subjectTokenType, scope);
        validateSubjectTokenIssuer(subjectIssuer);
        Map<String, Claim> claims = validateSubjectToken(subjectToken);
        log.info("SubjectToken authorized");
        return claims;
    }

    public void validateClient(String clientId) {
        if (!ALLOWED_CLIENT_ID.equals(clientId)){
            throw new InvalidExchangeClientException("Invalid clientId " + clientId);
        }
    }

    private void validateProtocolConfiguration(String grantType, String subjectTokenType, String scope) {
        if (!StringUtils.hasText(subjectTokenType)) {
            throw new InvalidExchangeRequestException("subjectTokenType is mandatory with token-exchange grant type");
        }
        if (!ALLOWED_GRANT_TYPE.equals(grantType)){
            throw new InvalidGrantTypeException("Invalid grantType " + grantType);
        }
        if (!ALLOWED_SUBJECT_TOKEN_TYPE.equals(subjectTokenType)){
            throw new InvalidTokenException("Invalid subjectTokenType " + subjectTokenType);
        }
        if (!ALLOWED_SCOPE.equals(scope)){
            throw new InvalidExchangeRequestException("Invalid scope " + scope);
        }
    }

    private void validateSubjectTokenIssuer(String subjectIssuer) {
        if (!StringUtils.hasText(subjectIssuer)) {
            throw new InvalidExchangeRequestException("subjectIssuer is mandatory with token-exchange grant type");
        }
        if (!allowedIssuer.equals(subjectIssuer)){
            throw new InvalidTokenIssuerException("Invalid subjectIssuer " + subjectIssuer);
        }
    }

    private Map<String, Claim> validateSubjectToken(String subjectToken) {
        if (!StringUtils.hasText(subjectToken)) {
            throw new InvalidExchangeRequestException("subjectToken is mandatory with token-exchange grant type");
        }
        Map<String, Claim> claims = jwtValidator.validate(subjectToken, urlJwkProvider);
        if (!allowedAudience.equals(claims.get(Claims.AUDIENCE).asString())){
            throw new InvalidTokenException("Invalid audience: " + allowedAudience);
        }
        if (!allowedIssuer.equals(claims.get(Claims.ISSUER).asString())){
            throw new InvalidTokenException("Invalid issuer: " + allowedIssuer);
        }
        return claims;
    }
}
