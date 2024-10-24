package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClientRepository extends ClientRepositoryExt, MongoRepository<Client, String> {
	List<Client> findAllByOrganizationIpaCode(String organizationIpaCode);
}
