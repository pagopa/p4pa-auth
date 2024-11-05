package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "m2m.legacy.public")
@Data
public class A2AClientLegacyPropConfig {
	private Map<String, String> secrets;
}
