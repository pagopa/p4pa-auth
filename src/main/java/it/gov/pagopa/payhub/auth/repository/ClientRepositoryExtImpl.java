package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ClientRepositoryExtImpl implements  ClientRepositoryExt {
	private final MongoTemplate mongoTemplate;

	public ClientRepositoryExtImpl(MongoTemplate mongoTemplate) { this.mongoTemplate = mongoTemplate;	}

	@Override
	public Client registerClient(String clientId, String organizationIpaCode, String clientSecret) {
		return mongoTemplate.findAndModify(
			Query.query(Criteria.where(Client.Fields.clientId).is(clientId)),
			new Update()
				.set(Client.Fields.organizationIpaCode, organizationIpaCode)
				.set(Client.Fields.clientSecret, clientSecret),
			FindAndModifyOptions.options()
				.returnNew(true)
				.upsert(true),
			Client.class
		);
	}
}
