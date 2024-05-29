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
class ValidateTokenService {
    public static final String ALLOWED_CLIENT_ID = "piattaforma-unitaria";
    public static final String ALLOWED_GRANT_TYPE="urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String ALLOWED_SUBJECT_TOKEN_TYPE="urn:ietf:params:oauth:token-type:id_token";
    public static final String ALLOWED_SCOPE="openid";

    private final String allowedAudience;
    private final String allowedIssuer;
    private final String urlJwkProvider;
    private final JWTValidator jwtValidator;

    public ValidateTokenService(@Value("${jwt.token.audience:}")String allowedAudience,
                           @Value("${jwt.token.issuer:}")String allowedIssuer,
                           @Value("${jwt.token.jwk:}")String urlJwkProvider,
                           JWTValidator jwtValidator) {
        this.allowedAudience = allowedAudience;
        this.allowedIssuer = allowedIssuer;

        this.urlJwkProvider = urlJwkProvider;
        this.jwtValidator = jwtValidator;
    }

    public void validate(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        validateClient(clientId);
        validateProtocolConfiguration(grantType, subjectTokenType, scope);
        validateSubjectTokenIssuer(subjectIssuer);
        validateSubjectToken(subjectToken);
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

    private void validateSubjectToken(String subjectToken) {
        Map<String, String> data = jwtValidator.validate(subjectToken, urlJwkProvider);
        if (!allowedAudience.equals(data.get(Claims.AUDIENCE))){
            throw new InvalidTokenException("Invalid audience: " + allowedAudience);
        }
        if (!allowedIssuer.equals(data.get(Claims.ISSUER))){
            throw new InvalidTokenException("Invalid issuer: " + allowedIssuer);
        }
    }
}
