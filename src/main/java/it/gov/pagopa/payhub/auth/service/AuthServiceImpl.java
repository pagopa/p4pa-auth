package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.InvalidTokenException;
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

    public AuthServiceImpl(@Value("${auth.token.audience:}")String audience,
                           @Value("${auth.token.issuer:}")String issuer,
                           @Value("${auth.token.jwk:}")String urlJwkProvider) {
        this.audience = audience;
        this.issuer = issuer;
        this.urlJwkProvider = urlJwkProvider;
    }

    @Override
    public void authToken(String token) {
        Map<String, Claim> data = JWTValidator.validate(token, urlJwkProvider);
        if (!(data.get("aud").asString().equals(audience) && data.get("iss").asString().equals(issuer))){
            throw new InvalidTokenException("Invalid audience or issuer in the token");
        }
    }
}
