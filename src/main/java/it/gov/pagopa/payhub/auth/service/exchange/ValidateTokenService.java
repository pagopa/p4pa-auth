package it.gov.pagopa.payhub.auth.service.exchange;

import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
class ValidateTokenService {
    private final String audience;
    private final String issuer;
    private final String urlJwkProvider;
    private final JWTValidator jwtValidator;

    public ValidateTokenService(@Value("${jwt.token.audience:}")String audience,
                           @Value("${jwt.token.issuer:}")String issuer,
                           @Value("${jwt.token.jwk:}")String urlJwkProvider,
                           JWTValidator jwtValidator) {
        this.audience = audience;
        this.issuer = issuer;
        this.urlJwkProvider = urlJwkProvider;
        this.jwtValidator = jwtValidator;
    }

    public void validate(String token) {
        Map<String, String> data = jwtValidator.validate(token, urlJwkProvider);
        if (!(data.get(Claims.AUDIENCE).equals(audience) && data.get(Claims.ISSUER).equals(issuer))){
            throw new InvalidTokenException("Invalid audience or issuer in the token");
        }
        log.info("Token validated successfully");
    }
}
