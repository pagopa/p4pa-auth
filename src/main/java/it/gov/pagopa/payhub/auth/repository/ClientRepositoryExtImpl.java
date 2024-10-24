package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ClientRepositoryExtImpl implements ClientRepositoryExt {
	private final MongoTemplate mongoTemplate;

	public ClientRepositoryExtImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void deleteClient(String organizationIpaCode, String clientId) {
		//find User with clientId
		Client client = mongoTemplate.findOne(Query.query(Criteria.where(Client.Fields.clientId).is(clientId)), Client.class);
		//If exists delete Client
		if(client != null)
			mongoTemplate.remove(
				Query.query(Criteria
					.where(Client.Fields.organizationIpaCode).is(organizationIpaCode)
					.and(Client.Fields.clientId).is(client.getClientId())), Client.class);
	}

}
