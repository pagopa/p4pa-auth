package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.RegisteredClaims;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class ValidateJWTLegacyService {
	public static final String TOKEN_TYPE_A2A = "a2a";

	private final A2ALegacySecretsRetrieverService a2ALegacySecretsRetrieverService;
	private final JWTValidator jwtValidator;

	public ValidateJWTLegacyService(A2ALegacySecretsRetrieverService a2ALegacySecretsRetrieverService, JWTValidator jwtValidator) {
		this.a2ALegacySecretsRetrieverService = a2ALegacySecretsRetrieverService;
		this.jwtValidator = jwtValidator;
	}

	public Pair<String, Map<String, Claim>> validate(String token) {
		Map<String, PublicKey> clientApplicationsPublicKeyMap = a2ALegacySecretsRetrieverService.envToMap();
		verifyEnvMap(clientApplicationsPublicKeyMap);
		Pair<String, Map<String, Claim>> claims = clientApplicationsPublicKeyMap.keySet().stream()
			.map(key -> new ImmutablePair<>(key, clientApplicationsPublicKeyMap.get(key)))
			.map(pair -> jwtValidator.validateLegacyToken(pair.getLeft(), token, pair.getRight()))
			.findFirst()
			.orElseThrow(() -> new InvalidTokenException("Invalid token for A2A call"));
		isM2MAuthToken(claims.getRight());
		validateClaims(claims.getRight());
		validateSubject(claims.getLeft());
		return claims;
	}

	private void verifyEnvMap(Map<String, PublicKey> clientApplicationsPublicKeyMap) {
		if (clientApplicationsPublicKeyMap.isEmpty()){
			throw new InvalidTokenException("The token is not present");
		}
	}

	private void isM2MAuthToken(Map<String, Claim> claims){
		if (TOKEN_TYPE_A2A.equals(claims.get("type").asString()))
			throw new InvalidTokenException("Invalid token type");
	}

	private void validateClaims(Map<String, Claim> claims) {
		if (claims.get(RegisteredClaims.ISSUED_AT).asInstant().isBefore(Instant.now().minusSeconds(3_600L * 24))) {
			throw new InvalidTokenException("Invalid field iat");
		}
		if (claims.get(RegisteredClaims.EXPIRES_AT).asInstant().isAfter(Instant.now().plusSeconds(3_600L * 24))) {
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
}
