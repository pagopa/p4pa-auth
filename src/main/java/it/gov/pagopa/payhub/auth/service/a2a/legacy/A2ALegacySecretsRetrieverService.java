package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import io.jsonwebtoken.io.Decoders;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class A2ALegacySecretsRetrieverService {
	private final A2AClientLegacyPropConfig a2AClientLegacyPropConfig;

	public A2ALegacySecretsRetrieverService(A2AClientLegacyPropConfig a2AClientLegacyPropConfig) {
		this.a2AClientLegacyPropConfig = a2AClientLegacyPropConfig;
	}

	public Map<String, PublicKey> envToMap() {
		return Optional.ofNullable(a2AClientLegacyPropConfig.getSecrets())
			.orElse(Map.of())
			.entrySet().stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				entry -> getPublicKeyFromString(entry.getValue())
			));
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
