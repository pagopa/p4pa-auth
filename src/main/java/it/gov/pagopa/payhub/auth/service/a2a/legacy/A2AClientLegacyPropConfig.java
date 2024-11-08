package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "m2m.legacy")
@Data
public class A2AClientLegacyPropConfig {
	private Map<String, String> publicKeys;

	public Map<String, PublicKey> getPublicKeysAsMap() {
		return Optional.ofNullable(publicKeys)
			.orElse(Map.of())
			.entrySet().stream()
			.collect(Collectors.toUnmodifiableMap(
				Map.Entry::getKey,
				entry -> getPublicKeyFromString(entry.getKey(), entry.getValue())
			));
	}

	private PublicKey getPublicKeyFromString(String keyName, String encodedKey) {
		try {
			X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedKey));
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(publicKeyX509);
		} catch (Exception e){
			throw new InvalidTokenException("invalid public key for: " + keyName);
		}
	}
}
