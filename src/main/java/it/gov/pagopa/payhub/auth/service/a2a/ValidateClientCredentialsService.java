package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ValidateClientCredentialsService {
	public static final String ALLOWED_GRANT_TYPE = "client_credentials";
	public static final String ALLOWED_SCOPE = "openid";

	public void validate(String scope, String clientSecret) {
		validateProtocolConfiguration(scope);
		validateClientSecret(clientSecret);
		log.debug("authorization granted");
	}

	private void validateProtocolConfiguration(String scope) {
		if (!ALLOWED_SCOPE.equals(scope)){
			throw new ClientUnauthorizedException("Invalid scope " + scope);
		}
	}

	private void validateClientSecret(String clientSecret) {
		if (!StringUtils.hasText(clientSecret)) {
			throw new ClientUnauthorizedException("clientSecret is mandatory with client-credentials grant type");
		}
	}

}
