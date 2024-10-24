package it.gov.pagopa.payhub.auth.repository;

public interface ClientRepositoryExt {
	void deleteClient(String organizationIpaCode, String clientId);
}
