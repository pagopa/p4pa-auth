package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientCredentialServiceImpl implements ClientCredentialService {

	private final ValidateClientCredentialsService validateClientCredentialsService;
	private final AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService;

	public ClientCredentialServiceImpl(ValidateClientCredentialsService validateClientCredentialsService, AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService) {
		this.validateClientCredentialsService = validateClientCredentialsService;
		this.authorizeClientCredentialsRequestService = authorizeClientCredentialsRequestService;
	}

	@Override
	public AccessToken postToken(String clientId, String scope, String clientSecret) {
		log.info("Client {} requested authentication with client_credentials grant type and scope {}", clientId, scope);
		validateClientCredentialsService.validate(scope, clientSecret);
		authorizeClientCredentialsRequestService.authorizeCredentials(clientId, clientSecret);
		return AccessToken.builder().accessToken("accessToken").build();
	}
}
