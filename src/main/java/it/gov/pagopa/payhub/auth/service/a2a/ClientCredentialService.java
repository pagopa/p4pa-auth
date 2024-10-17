package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.model.generated.AccessToken;

public interface ClientCredentialService {
	AccessToken postToken(String clientId, String grantType, String scope, String clientSecret);
}
