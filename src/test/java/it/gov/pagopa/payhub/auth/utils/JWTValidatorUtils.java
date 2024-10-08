package it.gov.pagopa.payhub.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.io.StringWriter;
import java.security.PublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class JWTValidatorUtils {

    private final WireMockServer wireMockServer;

    private static final String AUD = "AUD";
    private static final String ISS = "ISS";
    private static final String ACCESS_TOKEN_TYPE = "at+JWT";
    private static final String PUBLIC_KEY= "-----BEGIN PUBLIC KEY----- MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsyutJMN8Rc4gOpnjYpKO SFUoBo7eOMGThwpDaFDoHAbihwsYwIG3f5sbT1hhseSA31nqRZwiOJO7Sf55cI1Q 1pA7hcUehYBb6M06kjV42D8dnOuJjR0oNgajgclkfTayvHy21BIYo34lzRvvCszW 0u1yLxGFP0PROnFdY3rgUpXus0/du0Of5gEazmclYw+qsrju8iZM7932ZbqPUy5V ulWrE/iI7DYQT9tnJEaI5qtSY8KbneVL/RH9FabM97gT5ntmS27bwOjEaFYEU4R5 DXyX8coB+giRmZ+nffi8kIqZrbptiLHXE/mg3VRdX7XFF6UNsDkobw3xMJcMErsi ewIDAQAB -----END PUBLIC KEY-----";

    public JWTValidatorUtils(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    public String generateJWK(Date expiresAt) throws Exception {
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String token = generateToken(keyPair, expiresAt);

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

        return token;
    }

    public static String generateToken(KeyPair keyPair, Date expiresAt) {
        return JWT.create()
                .withIssuer(ISS)
                .withAudience(AUD)
                .withKeyId("my-key-id")
                .withJWTId("my-jwt-id")
                .withExpiresAt(expiresAt)
                .sign(Algorithm.RSA256((RSAPrivateKey) keyPair.getPrivate()));
    }

    public String getUrlJwkProvider() {
        return "http://localhost:" + wireMockServer.port() + "/jwks";
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public String generateInternalToken(KeyPair keyPair) throws Exception {
        Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", ACCESS_TOKEN_TYPE);
        String tokenType = "bearer";
        return JWT.create()
            .withHeader(headerClaims)
            .withClaim("typ", tokenType)
            .withIssuer(ISS)
            .withJWTId("my-jwt-id")
            .withIssuedAt(Instant.now())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm);
    }

    public static String getPublicKey(KeyPair keyPair) throws Exception {
        PublicKey publicKey = keyPair.getPublic();
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }
}
