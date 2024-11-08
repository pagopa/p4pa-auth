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

	private final A2ALegacySecretsService a2ALegacySecretsService;
	private final JWTValidator jwtValidator;

	public ValidateJWTLegacyService(A2ALegacySecretsService a2ALegacySecretsService, JWTValidator jwtValidator) {
		this.a2ALegacySecretsService = a2ALegacySecretsService;
		this.jwtValidator = jwtValidator;
	}

	public Pair<String, Map<String, Claim>> validate(String token) {
		Map<String, PublicKey> clientApplicationsPublicKeyMap = a2ALegacySecretsService.getLegacySecrets();
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

	private Pair<String, Map<String, Claim>> validateToken(Map<String, PublicKey> clientApplicationsPublicKeyMap, String token) {
		return clientApplicationsPublicKeyMap.keySet().stream()
			.map(key -> Pair.of(key, jwtValidator.validate(token, clientApplicationsPublicKeyMap.get(key))))
			.findFirst()
			.orElseThrow(() -> new InvalidTokenException("Invalid token for A2A call"));
	}
}
