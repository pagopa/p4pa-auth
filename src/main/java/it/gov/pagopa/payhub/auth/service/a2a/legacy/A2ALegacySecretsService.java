package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Map;

@Service
@Slf4j
public class A2ALegacySecretsService {
	private final A2AClientLegacyPropConfig a2AClientLegacyPropConfig;

	public A2ALegacySecretsService(A2AClientLegacyPropConfig a2AClientLegacyPropConfig) {
		this.a2AClientLegacyPropConfig = a2AClientLegacyPropConfig;
	}

	public Map<String, PublicKey> getLegacySecrets() {
		return a2AClientLegacyPropConfig.getPublicKeysAsMap();
	}
}
