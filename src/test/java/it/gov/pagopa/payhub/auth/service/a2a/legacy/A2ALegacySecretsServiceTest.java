package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class A2ALegacySecretsServiceTest {

	@Mock
	private A2AClientLegacyPropConfig a2aClientLegacyPropConfigMock;

	private A2ALegacySecretsService service;

	private KeyPair keyPair;

	@BeforeEach
	void setUp() throws Exception {
		service = new A2ALegacySecretsService(a2aClientLegacyPropConfigMock);
		keyPair = JWTValidatorUtils.generateKeyPair();
	}

	@Test
	void testGetLegacySecrets() {
		//Given
		PublicKey publicKey = keyPair.getPublic();
		when(a2aClientLegacyPropConfigMock.getPublicKeysAsMap()).thenReturn(Map.of("A2A-IPA_TEST_1", publicKey));
		//When
		Map<String, PublicKey> publicKeyMap = service.getLegacySecrets();
		//Then
		Assertions.assertTrue(publicKeyMap.containsKey("A2A-IPA_TEST_1"));
		Assertions.assertEquals(publicKey, publicKeyMap.get("A2A-IPA_TEST_1"));
	}
}
