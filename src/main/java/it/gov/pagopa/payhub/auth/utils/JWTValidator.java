package it.gov.pagopa.payhub.auth.utils;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;


/**
 * Utility class for validating JSON Web Tokens (JWTs).
 * This class uses Auth0's JWT and JWK libraries to decode, verify, and extract claims from a JWT.
 */
@Component
public class JWTValidator {

    /**
     * Validates a JWT against a JWK provider URL.
     *
     * @param token the JWT to validate
     * @param urlJwkProvider the URL of the JWK provider to use for validating the token
     * @return a map of claims extracted from the JWT
     * @throws TokenExpiredException if the token has expired
     * @throws InvalidTokenException if the token is invalid for any other reason
     */

    public Map<String, Claim> validate(String token, String urlJwkProvider) {
        try {
            DecodedJWT jwt = JWT.decode(token);

            JwkProvider provider = new UrlJwkProvider(urlJwkProvider);
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);

            return jwt.getClaims();

        } catch (com.auth0.jwt.exceptions.TokenExpiredException e){
            throw new TokenExpiredException(e.getMessage());
        } catch (JwkException | JWTVerificationException ex) {
            throw new InvalidTokenException("The token is not valid");
        }
    }

    /**
     * Validates JWT signature with publickey.
     *
     * @param token the JWT to validate
     * @throws InvalidKeySpecException if cannot load public key
     * @throws NoSuchAlgorithmException if cannot load public key with specified algorithm
     * @throws IOException if cannot load public key
     * @throws TokenExpiredException if the token has expired
     * @throws InvalidTokenException if the token is invalid for any other reason
     */
    public void validateInternalToken(String token, String pubKey) {
        RSAPublicKey rsaPublicKey = null;
        try{
            rsaPublicKey = CertUtils.pemPub2PublicKey(pubKey);
            Algorithm algorithm = Algorithm.RSA512(rsaPublicKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Cannot load public key", e);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException e){
            throw new TokenExpiredException(e.getMessage());
        } catch (JWTVerificationException ex) {
            throw new InvalidTokenException("The token is not valid");
        }
    }
}
