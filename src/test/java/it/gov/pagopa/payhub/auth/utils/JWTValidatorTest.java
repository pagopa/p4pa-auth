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

import java.util.Date;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
class JWTValidatorTest {

    private JWTValidator jwtValidator;
    private WireMockServer wireMockServer;
    private JWTValidatorUtils utils;

    @BeforeEach
    void setup(){
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        utils = new JWTValidatorUtils(wireMockServer);
        jwtValidator = new JWTValidator();
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
    void givenValidInternalJWTThenOk() throws Exception {
        KeyPair keyPair = JWTValidatorUtils.generateKeyPair();
        String validToken = utils.generateInternalToken(keyPair,new Date(System.currentTimeMillis() + 3600000));
        String publicKey = JWTValidatorUtils.getPublicKey(keyPair);

        Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(validToken, publicKey));
    }

    @Test
    void givenInvalidInternalJWTThenInvalidTokenException() throws Exception {
        KeyPair otherKeyPair = JWTValidatorUtils.generateKeyPair();
        String invalidToken = utils.generateInternalToken(otherKeyPair, new Date(System.currentTimeMillis() + 3600000));

        KeyPair keyPair = JWTValidatorUtils.generateKeyPair();
        String publicKey = JWTValidatorUtils.getPublicKey(keyPair);

        assertThrows(InvalidTokenException.class, () -> jwtValidator.validateInternalToken(invalidToken, publicKey));
    }

    @Test
    void givenTokenExpiredThenTokenExpiredException() throws Exception {
        KeyPair keyPair = JWTValidatorUtils.generateKeyPair();
        String invalidToken = utils.generateInternalToken(keyPair, new Date(System.currentTimeMillis() - 3600000));
        String publicKey = JWTValidatorUtils.getPublicKey(keyPair);

        assertThrows(TokenExpiredException.class, () -> jwtValidator.validateInternalToken(invalidToken, publicKey));
    }

}
