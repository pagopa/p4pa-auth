package it.gov.pagopa.payhub.auth.utils;

import com.auth0.jwt.interfaces.Claim;
import com.github.tomakehurst.wiremock.WireMockServer;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import java.security.KeyPair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class JWTValidatorTest {

    private JWTValidator jwtValidator;
    private WireMockServer wireMockServer;
    private JWTValidatorUtils utils;
    private KeyPair keyPair;

    @BeforeEach
    void setup() throws Exception {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        utils = new JWTValidatorUtils(wireMockServer);
        keyPair = JWTValidatorUtils.generateKeyPair();
        String publicKey = JWTValidatorUtils.getPublicKey(keyPair);
        jwtValidator = new JWTValidator(publicKey);
    }

    @AfterEach
    void clean(){
        wireMockServer.stop();
    }

    @Test
    void givenValidTokenThenSuccess() throws Exception {
        String token = utils.generateJWK(new Date(System.currentTimeMillis() + 3600000));

        String urlJwkProvider = utils.getUrlJwkProvider();

        Map<String, Claim> claimsMap = jwtValidator.validate(token, urlJwkProvider);

        assertNotNull(claimsMap);
    }

    @Test
    void givenTokenWithExpiredDateThenThrowTokenExpiredException() throws Exception {
        String expiredToken = utils.generateJWK(new Date(System.currentTimeMillis() - 3600000));
        String urlJwkProvider = utils.getUrlJwkProvider();

        assertThrows(TokenExpiredException.class, () -> jwtValidator.validate(expiredToken, urlJwkProvider));
    }

    @Test
    void givenInvalidTokenThenThrowInvalidTokenException() {
        String invalidToken = "your_invalid_token_here";
        String urlJwkProvider = "your_jwk_provider_url_here";

        assertThrows(InvalidTokenException.class, () -> jwtValidator.validate(invalidToken, urlJwkProvider));
    }

    @Test
    void givenValidInternalJWTThenOk() {
        String validToken = utils.generateInternalToken(keyPair,new Date(System.currentTimeMillis() + 3600000));
        Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(validToken));
    }

    @Test
    void givenInvalidInternalJWTThenInvalidTokenException() throws Exception {
        KeyPair otherKeyPair = JWTValidatorUtils.generateKeyPair();
        String invalidToken = utils.generateInternalToken(otherKeyPair, new Date(System.currentTimeMillis() + 3600000));

        assertThrows(InvalidTokenException.class, () -> jwtValidator.validateInternalToken(invalidToken));
    }

    @Test
    void givenTokenExpiredThenTokenExpiredException() {
        String invalidToken = utils.generateInternalToken(keyPair, new Date(System.currentTimeMillis() - 3600000));

        assertThrows(TokenExpiredException.class, () -> jwtValidator.validateInternalToken(invalidToken));
    }

    @Test
    void givenValidLegacyJWTThenOk() {
        String validToken = utils.generateLegacyToken(keyPair, "a2a", Instant.now(), Instant.now().plusSeconds(3_600_000L), "jwtId");
        Assertions.assertDoesNotThrow(() -> jwtValidator.validate(validToken, keyPair.getPublic()));
    }

    @Test
    void givenInvalidTokenWhenValidateLegacyTokenThenThrowInvalidTokenException() {
        String invalidToken = "your_invalid_token_here";
        PublicKey publicKey = keyPair.getPublic();
        assertThrows(InvalidTokenException.class, () ->jwtValidator.validate(invalidToken, publicKey));
    }

    @Test
    void givenInvalidTokenWhenValidateLegacyTokenThenThrowTokenExpiredException() {
        String invalidToken = utils.generateLegacyToken(keyPair, "a2a", Instant.now(), Instant.now().minusSeconds(3_600_000L), "jwtId");
        PublicKey publicKey = keyPair.getPublic();
        assertThrows(TokenExpiredException.class, () ->jwtValidator.validate(invalidToken, publicKey));
    }
}
