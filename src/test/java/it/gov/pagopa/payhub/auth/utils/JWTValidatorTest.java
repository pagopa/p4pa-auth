package it.gov.pagopa.payhub.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.github.tomakehurst.wiremock.WireMockServer;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import java.security.interfaces.RSAPublicKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    private RSAPublicKey rsaPublicKey;

    @Mock
    private JWTVerifier jwtVerifier;

    private static final String PUBLIC_KEY= """
        -----BEGIN PUBLIC KEY----- MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsyutJMN8Rc4gOpnjYpKO SFUoBo7eOMGThwpDaFDoHAbihwsYwIG3f5sbT1hhseSA31nqRZwiOJO7Sf55cI1Q 1pA7hcUehYBb6M06kjV42D8dnOuJjR0oNgajgclkfTayvHy21BIYo34lzRvvCszW 0u1yLxGFP0PROnFdY3rgUpXus0/du0Of5gEazmclYw+qsrju8iZM7932ZbqPUy5V ulWrE/iI7DYQT9tnJEaI5qtSY8KbneVL/RH9FabM97gT5ntmS27bwOjEaFYEU4R5 DXyX8coB+giRmZ+nffi8kIqZrbptiLHXE/mg3VRdX7XFF6UNsDkobw3xMJcMErsi ewIDAQAB -----END PUBLIC KEY-----
        """;

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
        String validToken = utils.generateInternalToken();
        Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(validToken, JWTValidatorUtils.getKeyPair().getPublic().toString()));
    }
}
