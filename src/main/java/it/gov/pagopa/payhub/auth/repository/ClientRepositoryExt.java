package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;

public interface ClientRepositoryExt {

	Client registerClient(String clientId, String organizationIpaCode, String clientSecret);
}
