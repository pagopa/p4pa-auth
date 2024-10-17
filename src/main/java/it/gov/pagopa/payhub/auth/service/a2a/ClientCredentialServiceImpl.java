package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientCredentialServiceImpl implements ClientCredentialService {

	private final ValidateClientCredentialsService validateClientCredentialsService;

	public ClientCredentialServiceImpl(ValidateClientCredentialsService validateClientCredentialsService) {
		this.validateClientCredentialsService = validateClientCredentialsService;
	}

	@Override
	public AccessToken postToken(String clientId, String grantType, String scope, String clientSecret) {
		validateClientCredentialsService.validate(clientId, grantType, scope, clientSecret);
		return new AccessToken("fakeToken", "bearer", 7);
	}
}
