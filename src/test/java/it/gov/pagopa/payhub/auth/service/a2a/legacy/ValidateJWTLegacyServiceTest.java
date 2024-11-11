package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ValidateJWTLegacyServiceTest {

	@Mock
	private A2AClientLegacyPropConfig a2AClientLegacyPropConfig;
	@Mock
	private JWTValidator jwtValidatorMock;
	private ValidateJWTLegacyService service;
	private KeyPair keyPair1;
	private KeyPair keyPair2;

	@BeforeEach
	void setup() throws Exception {
		keyPair1 = JWTValidatorUtils.generateKeyPair();
		keyPair2 = JWTValidatorUtils.generateKeyPair();
		Map<String, PublicKey> publicKeyMap = Map.of(
			"A2A-IPA_TEST_1", keyPair1.getPublic(),
			"A2A-IPA_TEST_2", keyPair2.getPublic());
		when(a2AClientLegacyPropConfig.getPublicKeysAsMap()).thenReturn(publicKeyMap);
		service = new ValidateJWTLegacyService(a2AClientLegacyPropConfig, jwtValidatorMock);
	}

	@Test
	void GivenValidTokenThenOk() {
		String appName = "A2A-IPA_TEST_2";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair2, "a2a", Instant.now(), Instant.now().plusSeconds(3_600_000L), "jti");

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, keyPair2.getPublic())).thenReturn(claimsMap);

		Pair<String, Map<String, Claim>> result = service.validate(token);

		assertEquals(Pair.of(appName, claimsMap), result);
	}

	@Test
	void GivenInvalidTokenThenInvalidTokenException() {
		String token = JWTValidatorUtils.generateLegacyToken(keyPair1, "a2a", Instant.now(), Instant.now().plusSeconds(3_600_000L), "jti");

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, keyPair2.getPublic())).thenReturn(claimsMap);

		assertThrows(Exception.class, () -> service.validate("invalidToken"), "given an invalid token");
	}

	@Test
	void GivenNonM2MAuthTokenThenInvalidTokenException() {
		PublicKey publicKey = keyPair2.getPublic();
		String token = JWTValidatorUtils.generateLegacyToken(keyPair2, "notA2A", Instant.now(), Instant.now().plusSeconds(3_600L), "jwtId");
		when(a2AClientLegacyPropConfig.getPublicKeysAsMap()).thenReturn(Map.of("A2A-IPA_TEST_1", publicKey));

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, publicKey)).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid token type");
	}

	@Test
	void GivenInvalidIatThenInvalidTokenException() {
		PublicKey publicKey = keyPair2.getPublic();
		String token = JWTValidatorUtils.generateLegacyToken(keyPair2, "a2a", Instant.now().plusSeconds(3_600L), Instant.now().plusSeconds(3_600_000L), "jwtId");


		when(a2AClientLegacyPropConfig.getPublicKeysAsMap()).thenReturn(Map.of("A2A-IPA_TEST_1", publicKey));

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, publicKey)).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field iat");
	}

	@Test
	void GivenInvalidExpThenInvalidTokenException() {
		PublicKey publicKey = keyPair2.getPublic();
		String token = JWTValidatorUtils.generateLegacyToken(keyPair2, "a2a", Instant.now(), Instant.now().minusSeconds(3_600L), "jwtId");

		when(a2AClientLegacyPropConfig.getPublicKeysAsMap()).thenReturn(Map.of("A2A-IPA_TEST_1", publicKey));

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, publicKey)).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field exp");
	}

	@Test
	void GivenInvalidJtiThenInvalidTokenException() {
		PublicKey publicKey = keyPair2.getPublic();
		String token = JWTValidatorUtils.generateLegacyToken(keyPair2, "a2a", Instant.now(), Instant.now().plusSeconds(3_600L), "");

		when(a2AClientLegacyPropConfig.getPublicKeysAsMap()).thenReturn(Map.of("A2A-IPA_TEST_1", publicKey));

		Map<String, Claim> claimsMap = JWT.decode(token).getClaims();
		Mockito.when(jwtValidatorMock.validate(token, publicKey)).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field jti");
	}

}
