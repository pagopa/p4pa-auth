package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import org.springframework.data.mongodb.repository.Query;

public interface ClientRepository extends MongoRepository<Client, String> {
	List<Client> findAllByOrganizationIpaCode(String organizationIpaCode);
	long deleteByClientIdAndOrganizationIpaCode(String clientId, String organizationIpaCode);

	@Query(value = "{" +
			"    $and: [" +
			"        { $or: [{ $expr: { $eq: ['?0', 'null'] }}, { clientId: ?0 }] }," +
			"        { $or: [{ $expr: { $eq: ['?1', 'null'] }}, { clientName: { $gte: ?1 } }] }," +
			"        { $or: [{ $expr: { $eq: ['?2', 'null'] }}, { organizationIpaCode: ?2 }] }," +
			"    ] }", fields = "{bodyCiphered:  0}")
	Page<ClientNoSecretDTO> searchByFilters(
			String clientId,
			String clientName,
			String organizationIpaCode,
			Pageable pageable
	);
}
