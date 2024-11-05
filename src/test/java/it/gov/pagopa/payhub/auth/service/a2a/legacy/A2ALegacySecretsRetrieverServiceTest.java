package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import io.jsonwebtoken.io.Encoders;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.service.a2a.legacy.A2AClientLegacyPropConfig;
import it.gov.pagopa.payhub.auth.service.a2a.legacy.A2ALegacySecretsRetrieverService;
import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
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
	void setUp() throws Exception {
		service = new A2ALegacySecretsRetrieverService(a2aClientLegacyPropConfigMock);
		keyPair = JWTValidatorUtils.generateKeyPair();
	}

	@Test
	void givenEnvPropWhenEnvToMapThenInvokeA2ALegacySecretsRetrieverService() {
		//Given
		PublicKey publicKey = keyPair.getPublic();
		String publicKeyEncoded = Encoders.BASE64.encode(publicKey.getEncoded());
		Map<String, String> envMapProps = Map.of("acme", publicKeyEncoded);
		when(a2aClientLegacyPropConfigMock.getSecrets()).thenReturn(envMapProps);
		//When

		Map<String, PublicKey> result = service.envToMap();

		//Then
		assertNotNull(result);
		assertEquals(publicKey, result.get("acme"));
	}

	@Test
	void GivenInvalidPropsWhenEnvToMapThenInvalidTokenException() {
		//Given
		Map<String, String> envMapProps = new HashMap<>();
		envMapProps.put("nullProperty", null);
		envMapProps.put("emptyProperty", "");
		envMapProps.put("invalidBase64", "not_base64");
		when(a2aClientLegacyPropConfigMock.getSecrets()).thenReturn(envMapProps);
		//When Then
		Assertions.assertThrows(InvalidTokenException.class, () -> service.envToMap());
	}


}
