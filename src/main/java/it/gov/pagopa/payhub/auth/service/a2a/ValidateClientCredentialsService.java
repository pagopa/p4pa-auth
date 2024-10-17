package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeClientException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidExchangeRequestException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ValidateClientCredentialsService {
	private final ClientService clientService;
	public static final String ALLOWED_GRANT_TYPE = "client_credentials";
	public static final String ALLOWED_SCOPE = "openid";

	public ValidateClientCredentialsService(ClientService clientService) {
		this.clientService = clientService;
	}

	public void validate(String clientId, String grantType, String scope, String clientSecret) {
		validateClient(clientId);
		validateProtocolConfiguration(grantType, scope);
		validateClientSecret(clientSecret);
		log.info("authorization granted");
	}

	//TODO Client will be used to verify clientSecret and assign roles with organizationIpaCode
	private Client validateClient(String clientId) {
		return clientService.getClientByClientId(clientId)
			.orElseThrow(() -> new InvalidExchangeClientException("Invalid clientId:"+ clientId));
	}

	private void validateProtocolConfiguration(String grantType, String scope) {
		if (!ALLOWED_GRANT_TYPE.equals(grantType)) {
			throw new InvalidGrantTypeException("Invalid grantType " + grantType);
		}
		if (!ALLOWED_SCOPE.equals(scope)){
			throw new InvalidExchangeRequestException("Invalid scope " + scope);
		}
	}

	private void validateClientSecret(String clientSecret) {
		if (!StringUtils.hasText(clientSecret)) {
			throw new InvalidExchangeRequestException("clientSecret is mandatory with client-credentials grant type");
		}
	}

}
