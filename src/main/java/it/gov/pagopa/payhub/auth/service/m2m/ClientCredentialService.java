package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.dto.generated.AccessToken;

public interface ClientCredentialService {
	AccessToken postToken(String clientId, String scope, String clientSecret);
}
