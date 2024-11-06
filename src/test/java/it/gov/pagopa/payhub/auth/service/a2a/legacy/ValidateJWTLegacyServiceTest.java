package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ValidateJWTLegacyServiceTest {

	@Mock
	private A2ALegacySecretsRetrieverService a2ALegacySecretsRetrieverServiceMock;
	@Mock
	private JWTValidator jwtValidatorMock;
	private ValidateJWTLegacyService service;
	private KeyPair keyPair;


	@BeforeEach
	void setup() throws Exception {
		service = new ValidateJWTLegacyService(a2ALegacySecretsRetrieverServiceMock, jwtValidatorMock);
		keyPair = JWTValidatorUtils.generateKeyPair();
	}

	@Test
	void GivenValidTokenThenOk() {
		String appName = "APPLICATION_NAME";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair, "a2a", Instant.now(), Instant.now().plusSeconds(3_600L), "jti");

		Map<String, PublicKey> clientApplicationsPublicKeyMap = Map.of(appName, keyPair.getPublic());
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);

		Pair<String, Map<String, Claim>> claimsMap = new ImmutablePair<>(appName, JWT.decode(token).getClaims());
		Mockito.when(jwtValidatorMock.validateLegacyToken(appName, token, keyPair.getPublic())).thenReturn(claimsMap);

		Pair<String, Map<String, Claim>> result = service.validate(token);

		assertEquals(claimsMap, result);
	}

	@Test
	void GivenEmptyMapThenInvalidTokenException() {
		Map<String, PublicKey> clientApplicationsPublicKeyMap = new HashMap<>();
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);
		assertThrows(InvalidTokenException.class, () -> service.validate("token"));
	}

	@Test
	void GivenNonM2MAuthTokenThenInvalidTokenException() {
		String appName = "APPLICATION_NAME";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair, "notA2A", Instant.now(), Instant.now().plusSeconds(3_600L), "jwtId");

		Map<String, PublicKey> clientApplicationsPublicKeyMap = Map.of(appName, keyPair.getPublic());
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);

		Pair<String, Map<String, Claim>> claimsMap = new ImmutablePair<>(appName, JWT.decode(token).getClaims());
		Mockito.when(jwtValidatorMock.validateLegacyToken(appName, token, keyPair.getPublic())).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid token type");
	}

	@Test
	void GivenInvalidIatThenInvalidTokenException() {
		String appName = "APPLICATION_NAME";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair, "a2a", Instant.now().minusSeconds(3_600L * 48), Instant.now().plusSeconds(3_600_000L), "jwtId");

		Map<String, PublicKey> clientApplicationsPublicKeyMap = Map.of(appName, keyPair.getPublic());
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);

		Pair<String, Map<String, Claim>> claimsMap = new ImmutablePair<>(appName, JWT.decode(token).getClaims());
		Mockito.when(jwtValidatorMock.validateLegacyToken(appName, token, keyPair.getPublic())).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field iat");
	}

	@Test
	void GivenInvalidExpThenInvalidTokenException() {
		String appName = "APPLICATION_NAME";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair, "a2a", Instant.now(), Instant.now().plusSeconds(3_600L * 48), "jwtId");

		Map<String, PublicKey> clientApplicationsPublicKeyMap = Map.of(appName, keyPair.getPublic());
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);

		Pair<String, Map<String, Claim>> claimsMap = new ImmutablePair<>(appName, JWT.decode(token).getClaims());
		Mockito.when(jwtValidatorMock.validateLegacyToken(appName, token, keyPair.getPublic())).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field exp");
	}

	@Test
	void GivenInvalidJtiThenInvalidTokenException() {
		String appName = "APPLICATION_NAME";
		String token = JWTValidatorUtils.generateLegacyToken(keyPair, "a2a", Instant.now(), Instant.now().plusSeconds(3_600L), "");

		Map<String, PublicKey> clientApplicationsPublicKeyMap = Map.of(appName, keyPair.getPublic());
		Mockito.when(a2ALegacySecretsRetrieverServiceMock.envToMap()).thenReturn(clientApplicationsPublicKeyMap);

		Pair<String, Map<String, Claim>> claimsMap = new ImmutablePair<>(appName, JWT.decode(token).getClaims());
		Mockito.when(jwtValidatorMock.validateLegacyToken(appName, token, keyPair.getPublic())).thenReturn(claimsMap);

		assertThrows(InvalidTokenException.class, () -> service.validate(token), "Invalid field jti");
	}

}
