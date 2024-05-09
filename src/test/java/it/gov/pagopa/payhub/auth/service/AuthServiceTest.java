package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import it.gov.pagopa.payhub.auth.constants.AuthConstants;
import it.gov.pagopa.payhub.auth.exception.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AuthServiceTest {

    private AuthService authService;
    private WireMockServer wireMockServer;
    private static final String AUD = "AUD";
    private static final String ISS = "ISS";
    @Mock
    private JWTValidator jwtValidator;
    @Mock
    private Jwk jwk;
    private String setUp() throws Exception {
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        String token = generateToken(keyPair);
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        JWK jwk = new RSAKey.Builder(rsaPublicKey)
                .keyID("my-key-id")
                .build();
        JWKSet jwkSet = new JWKSet(jwk);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keys", jwkSet.toJSONObject().get("keys"));

        WireMock.configureFor("localhost", wireMockServer.port());
        stubFor(get(urlEqualTo("/jwks/.well-known/jwks.json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(jwkSet))));

        authService = new AuthServiceImpl(AUD, ISS, getUrlJwkProvider(), jwtValidator);

        return token;
    }

    @AfterEach
    void clean(){
        wireMockServer.stop();
    }
    @Test
    void authToken() throws Exception {
        String token = setUp();
        Map<String, String> claimsMap = createJWKClaims(ISS, AUD);

        String wireMockUrl = getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        authService.authToken(token);
        Mockito.verify(jwtValidator, times(1)).validate(token, wireMockUrl);
    }

    @Test
    void authTokenWrongIss() throws Exception {
        String token = setUp();
        Map<String, String> claimsMap = createJWKClaims("ISS_FAKE", AUD);

        String wireMockUrl = getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        InvalidTokenException result =
                assertThrows(InvalidTokenException.class, () ->
                                authService.authToken(token));

        assertEquals(AuthConstants.ExceptionCode.INVALID_TOKEN, result.getCode());
    }

    @Test
    void authTokenWrongAud() throws Exception {
        String token = setUp();
        Map<String, String> claimsMap = createJWKClaims(ISS, "AUD_FAKE");

        String wireMockUrl = getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        InvalidTokenException result =
                assertThrows(InvalidTokenException.class, () ->
                        authService.authToken(token));

        assertEquals(AuthConstants.ExceptionCode.INVALID_TOKEN, result.getCode());
    }

    public static String generateToken(KeyPair keyPair) {
        return JWT.create()
                .withIssuer(ISS)
                .withAudience(AUD)
                .withKeyId("my-key-id")
                .withJWTId("my-jwt-id")
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.RSA256((RSAPrivateKey) keyPair.getPrivate()));
    }

    private String getUrlJwkProvider() {
        return "http://localhost:" + wireMockServer.port() + "/jwks";
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private Map<String, String> createJWKClaims (String iss, String aud){
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", iss);
        claims.put("aud", aud);
        claims.put("exp", "1715267318");
        claims.put("jti", "my-key-id");
        return claims;
    }
}
