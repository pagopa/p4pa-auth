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

    public String generateInternalToken(KeyPair keyPair, Date expiresAt) {
        Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        String tokenType = "bearer";
        return JWT.create()
            .withClaim("typ", tokenType)
            .withIssuer(ISS)
            .withJWTId("my-jwt-id")
            .withIssuedAt(Instant.now())
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }

    public static String generateLegacyToken(KeyPair keyPair, String type, Instant issuedAt, Instant expiresAt, String jwtId) {
        Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        return JWT.create()
          .withClaim("type", type)
          .withJWTId(jwtId)
          .withIssuedAt(issuedAt)
          .withExpiresAt(expiresAt)
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
