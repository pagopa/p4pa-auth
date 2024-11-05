package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "m2m.legacy.public")
public class A2AClientLegacyPropConfig {
	private Map<String, String> secrets;
}
