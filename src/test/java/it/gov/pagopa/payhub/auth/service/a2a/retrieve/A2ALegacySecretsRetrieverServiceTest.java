package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import it.gov.pagopa.payhub.auth.service.a2a.legacy.A2AClientLegacyPropConfig;
import it.gov.pagopa.payhub.auth.service.a2a.legacy.A2ALegacySecretsRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class A2ALegacySecretsRetrieverServiceTest {

	@Mock
	private A2AClientLegacyPropConfig a2aClientLegacyPropConfigMock;

	private A2ALegacySecretsRetrieverService service;

	private KeyPair keyPair;

	@BeforeEach
	public void setUp() {
		service = new A2ALegacySecretsRetrieverService(a2aClientLegacyPropConfigMock);
		keyPair = Keys.keyPairFor(SignatureAlgorithm.RS512);
	}

	@Test
	public void givenEnvPropWhenEnvToMapByPrefixThenGetKey() {
		//Given
		String secretKeyPrefix = "m2m.legacy.public";
		PublicKey publicKey = keyPair.getPublic();
		String publicKeyEncoded = Encoders.BASE64.encode(keyPair.getPublic().getEncoded());
		Map<String, String> mockSettings = Map.of("acme", publicKeyEncoded);
		when(a2aClientLegacyPropConfigMock.getSecrets()).thenReturn(mockSettings);
		//When

		Map<String, PublicKey> result = service.envToMapByPrefix(secretKeyPrefix);

		//Then
		assertNotNull(result);
		assertEquals(publicKey, result.get("acme"));
	}

	@Test
	public void WhenEnvToMapByPrefixThenGetEmptyMap() {
		//When
		Map<String, PublicKey> result = service.envToMapByPrefix("nonExistent");
		//Then
		assertEquals(0, result.size());
	}
}
