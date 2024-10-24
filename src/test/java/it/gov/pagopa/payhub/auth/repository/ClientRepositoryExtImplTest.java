package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryExtImplTest {

	@Mock
	private MongoTemplate mongoTemplateMock;

	private ClientRepositoryExt repository;

	@BeforeEach
	void init() {
		repository = new ClientRepositoryExtImpl(mongoTemplateMock);
	}

	@AfterEach
	void verifyNotMoreInvocation() {
		Mockito.verifyNoMoreInteractions(mongoTemplateMock);
	}

	@Test
	void whenDeleteClientThenOk() {
		// Given
		String organizationIpaCode = "IPA_CODE";
		String clientId = "IPA_CODEclientId";
		Client client = Client.builder()
			.clientId(clientId)
			.organizationIpaCode(organizationIpaCode)
			.build();

		Mockito.when(mongoTemplateMock.findOne(
			Query.query(Criteria.where(Client.Fields.clientId).is(clientId)),
			Client.class)).thenReturn(client);

		// When
		repository.deleteClient(organizationIpaCode, clientId);
		// Then
		Mockito.verify(mongoTemplateMock).remove(
			Query.query(Criteria
				.where(Client.Fields.organizationIpaCode).is(client.getOrganizationIpaCode())
				.and(Client.Fields.clientId).is(client.getClientId())),
			Client.class);
	}
}
