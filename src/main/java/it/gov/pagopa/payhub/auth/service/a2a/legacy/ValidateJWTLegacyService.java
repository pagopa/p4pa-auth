package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.RegisteredClaims;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class ValidateJWTLegacyService {
	private static final String TOKEN_TYPE_A2A = "a2a";

	private final A2ALegacySecretsRetrieverService a2ALegacySecretsRetrieverService;
	private final Map<String, PublicKey> clientApplicationsPublicKey;

	public ValidateJWTLegacyService(A2ALegacySecretsRetrieverService a2ALegacySecretsRetrieverService) {
		this.a2ALegacySecretsRetrieverService = a2ALegacySecretsRetrieverService;
		this.clientApplicationsPublicKey = a2ALegacySecretsRetrieverService.envToMap();
	}

	public Pair<String, Map<String, Claim>> validate(String token) {
		verifyEnvMap();
		Pair<String, Map<String, Claim>> claims = this.clientApplicationsPublicKey.keySet().stream()
			.map(key -> new ImmutablePair<>(key, this.clientApplicationsPublicKey.get(key)))
			.map(pair -> validateLegacyToken(pair.getLeft(), token, pair.getRight()))
			.findFirst()
			.orElseThrow(() -> new InvalidTokenException("Invalid token for A2A call"));
		isA2AAuthToken(claims.getRight());
		validateClaims(claims.getRight());
		validateSubject(claims.getLeft());
		return claims;
	}

	private void verifyEnvMap() {
		if (this.clientApplicationsPublicKey.isEmpty()){
			throw new InvalidTokenException("The token is not present");
		}
	}

	public void isA2AAuthToken(Map<String, Claim> claims){
		if (TOKEN_TYPE_A2A.equals(claims.getOrDefault("type", null)))
			throw new InvalidTokenException("Invalid token type");
	}

	private void validateClaims(Map<String, Claim> claims) {
		if (claims.get(RegisteredClaims.ISSUED_AT).asInstant().isBefore(Instant.now().minusSeconds(3600 * 24))) {
			throw new InvalidTokenException("Invalid field iat");
		}
		if (claims.get(RegisteredClaims.EXPIRES_AT).asInstant().isAfter(Instant.now().plusSeconds(3600 * 24))) {
			throw new InvalidTokenException("Invalid field exp");
		}
		if (StringUtils.isBlank(claims.get(RegisteredClaims.JWT_ID).asString())) {
			throw new InvalidTokenException("Invalid field jti");
		}
	}

	private void validateSubject(String subject) {
		if (StringUtils.isBlank(subject)) {
			throw new InvalidTokenException("Invalid subject");
		}
	}

	private Pair<String, Map<String, Claim>> validateLegacyToken(String app, String token, PublicKey publicKey) {
		try{
			DecodedJWT jwt = JWT.decode(token);

			Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) publicKey);
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(jwt);

			return new ImmutablePair<>(app, jwt.getClaims());
		} catch (Exception e) {
			return null;
		}
	}
}
