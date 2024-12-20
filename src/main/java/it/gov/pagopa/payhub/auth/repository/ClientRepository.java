package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClientRepository extends MongoRepository<Client, String> {
	List<Client> findAllByOrganizationIpaCode(String organizationIpaCode);
	long deleteByClientIdAndOrganizationIpaCode(String clientId, String organizationIpaCode);
}
