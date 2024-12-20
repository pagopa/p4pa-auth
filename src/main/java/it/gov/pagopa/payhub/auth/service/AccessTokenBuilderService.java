package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.HeaderParams;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import it.gov.pagopa.payhub.auth.utils.CertUtils;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccessTokenBuilderService {
	public static final String ACCESS_TOKEN_TYPE = "at+JWT";
	private final String allowedAudience;
	private final int expireIn;
	private final RSAPublicKey rsaPublicKey;
	private final RSAPrivateKey rsaPrivateKey;
	private final String kid;

	public AccessTokenBuilderService(
		@Value("${jwt.audience}") String allowedAudience,
		@Value("${jwt.access-token.expire-in}") int expireIn,
		@Value("${jwt.access-token.private-key}") String privateKey,
		@Value("${jwt.access-token.public-key}") String publicKey, DataCipherService dataCipherService) {
		this.allowedAudience = allowedAudience;
		this.expireIn = expireIn;
		byte[] hashed = dataCipherService.hash(publicKey);
		this.kid = UUID.nameUUIDFromBytes(hashed).toString();

		try {
			rsaPrivateKey = CertUtils.pemKey2PrivateKey(privateKey);
			rsaPublicKey = CertUtils.pemPub2PublicKey(publicKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			throw new IllegalStateException("Cannot load private and/or public key", e);
		}
	}

	public AccessToken build(){
		Algorithm algorithm = Algorithm.RSA512(rsaPublicKey, rsaPrivateKey);
		Map<String, Object> headerClaims = new HashMap<>();
		headerClaims.put(HeaderParams.KEY_ID, kid);
		headerClaims.put("typ", ACCESS_TOKEN_TYPE);
		String tokenType = "bearer";
		String token = JWT.create()
  		.withHeader(headerClaims)
			.withClaim("typ", tokenType)
			.withIssuer(allowedAudience)
			.withJWTId(UUID.randomUUID().toString())
			.withIssuedAt(Instant.now())
			.withExpiresAt(Instant.now().plusSeconds(expireIn))
			.sign(algorithm);
		return new AccessToken(token, tokenType, expireIn);
	}

	public String getHeaderPrefix() {
		var prefix = String.format("{\"kid\":\"%s\"", kid);
		return Base64.getEncoder().encodeToString(prefix.getBytes());
	}
}
