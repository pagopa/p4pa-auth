package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import io.jsonwebtoken.io.Decoders;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class A2ALegacySecretsRetrieverService {
	private final A2AClientLegacyPropConfig a2AClientLegacyPropConfig;

	public A2ALegacySecretsRetrieverService(A2AClientLegacyPropConfig a2AClientLegacyPropConfig) {
		this.a2AClientLegacyPropConfig = a2AClientLegacyPropConfig;
	}

	public Map<String, PublicKey> envToMapByPrefix(String prefix) {
		Map<String, PublicKey> secretMap = new HashMap<>();
		a2AClientLegacyPropConfig.getSecrets()
			.forEach((key, value) ->
				secretMap.put(key, getPublicKeyFromString(value))
			);
		return secretMap;
	}

	private PublicKey getPublicKeyFromString(String encodedKey) {
		try {
			X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(Decoders.BASE64.decode(encodedKey));
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(publicKeyX509);
		} catch (Exception e){
			throw new InvalidTokenException("invalid public key");
		}
	}
}
