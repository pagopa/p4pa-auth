package it.gov.pagopa.payhub.auth.service;

import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{
    private final String audience;
    private final String issuer;
    private final String urlJwkProvider;
    private final JWTValidator jwtValidator;

    public AuthServiceImpl(@Value("${auth.token.audience:}")String audience,
                           @Value("${auth.token.issuer:}")String issuer,
                           @Value("${auth.token.jwk:}")String urlJwkProvider,
                           JWTValidator jwtValidator) {
        this.audience = audience;
        this.issuer = issuer;
        this.urlJwkProvider = urlJwkProvider;
        this.jwtValidator = jwtValidator;
    }

    @Override
    public void authToken(String token) {
        Map<String, String> data = jwtValidator.validate(token, urlJwkProvider);
        if (!(data.get(Claims.AUDIENCE).equals(audience) && data.get(Claims.ISSUER).equals(issuer))){
            throw new InvalidTokenException("Invalid audience or issuer in the token");
        }
        log.info("Token validated successfully");
    }
}
