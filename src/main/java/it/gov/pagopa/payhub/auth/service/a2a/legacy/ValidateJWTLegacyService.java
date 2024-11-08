package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.RegisteredClaims;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class ValidateJWTLegacyService {
	public static final String TOKEN_TYPE_A2A = "a2a";

	private final A2AClientLegacyPropConfig a2AClientLegacyPropConfig;
	private final JWTValidator jwtValidator;

	public ValidateJWTLegacyService(A2AClientLegacyPropConfig a2AClientLegacyPropConfig, JWTValidator jwtValidator) {
		this.a2AClientLegacyPropConfig = a2AClientLegacyPropConfig;
		this.jwtValidator = jwtValidator;
	}

	public Pair<String, Map<String, Claim>> validate(String token) {
		Map<String, PublicKey> clientApplicationsPublicKeyMap = a2AClientLegacyPropConfig.getPublicKeysAsMap();
		Pair<String, Map<String, Claim>> claims = validateToken(clientApplicationsPublicKeyMap, token);
		validateM2MType(claims.getRight());
		validateClaims(claims.getRight());

		return claims;
	}


	private void validateM2MType(Map<String, Claim> claims){
		if (!TOKEN_TYPE_A2A.equals(claims.get("type").asString()))
			throw new InvalidTokenException("Invalid token type");
	}

	private void validateClaims(Map<String, Claim> claims) {
		if (claims.get(RegisteredClaims.ISSUED_AT).asInstant().isAfter(Instant.now())) {
			throw new InvalidTokenException("Invalid field iat");
		}
		if (claims.get(RegisteredClaims.EXPIRES_AT).asInstant().isBefore(Instant.now())) {
			throw new InvalidTokenException("Invalid field exp");
		}
		if (StringUtils.isBlank(claims.get(RegisteredClaims.JWT_ID).asString())) {
			throw new InvalidTokenException("Invalid field jti");
		}
	}

	private Pair<String, Map<String, Claim>> validateToken(Map<String, PublicKey> clientApplicationsPublicKeyMap, String token) {
		try {
			return clientApplicationsPublicKeyMap.keySet().stream()
				.map(key -> Pair.of(key, jwtValidator.validate(token, clientApplicationsPublicKeyMap.get(key))))
				.findFirst()
				.orElseThrow(() -> new InvalidTokenException("Invalid token for A2A call"));
		} catch (Exception e) {
			return null;
		}
	}
}
