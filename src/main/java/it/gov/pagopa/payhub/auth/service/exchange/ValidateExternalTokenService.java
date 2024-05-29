package it.gov.pagopa.payhub.auth.service.exchange;

import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
class ValidateExternalTokenService {
    public static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    public static final String ALLOWED_GRANT_TYPE="urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String ALLOWED_SUBJECT_TOKEN_TYPE="urn:ietf:params:oauth:token-type:id_token";
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

    public Map<String, String> validate(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        validateClient(clientId);
        validateProtocolConfiguration(grantType, subjectTokenType, scope);
        validateSubjectTokenIssuer(subjectIssuer);
        Map<String, String> claims = validateSubjectToken(subjectToken);
        log.info("SubjectToken authorized");
        return claims;
    }

    private void validateClient(String clientId) {
        if (!ALLOWED_CLIENT_ID.equals(clientId)){
            throw new InvalidExchangeClientException("Invalid clientId " + clientId);
        }
    }

    private void validateProtocolConfiguration(String grantType, String subjectTokenType, String scope) {
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
        if (!allowedIssuer.equals(subjectIssuer)){
            throw new InvalidTokenIssuerException("Invalid subjectIssuer " + subjectIssuer);
        }
    }

    private Map<String, String> validateSubjectToken(String subjectToken) {
        Map<String, String> claims = jwtValidator.validate(subjectToken, urlJwkProvider);
        if (!allowedAudience.equals(claims.get(Claims.AUDIENCE))){
            throw new InvalidTokenException("Invalid audience: " + allowedAudience);
        }
        if (!allowedIssuer.equals(claims.get(Claims.ISSUER))){
            throw new InvalidTokenException("Invalid issuer: " + allowedIssuer);
        }
        return claims;
    }
}
