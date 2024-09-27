package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientSecretGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ClientRepositoryExtImplTest {

	@Mock
	private MongoTemplate mongoTemplateMock;

	@Mock
	private ClientSecretGeneratorService clientSecretGeneratorService;

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
	void whenRegisterClientThenReturnStoredClient() {

		String clientId="CLIENTID";
		String organizationIpaCode="ORGANIZATIONIPACODE";
		String clientSecret = clientSecretGeneratorService.apply(UUID.randomUUID().toString());
		Client storedClient = new Client();

		Mockito.when(mongoTemplateMock.findAndModify(
			Mockito.eq(Query.query(Criteria
				.where(Client.Fields.clientId).is(clientId))),
			Mockito.eq(new Update()
					.set(Client.Fields.organizationIpaCode, organizationIpaCode)
					.set(Client.Fields.clientSecret, clientSecret)),
			Mockito.argThat(opt -> opt.isReturnNew() && opt.isUpsert() && !opt.isRemove()),
			Mockito.eq(Client.class)
		)).thenReturn(storedClient);

		Client result = repository.registerClient(clientId, organizationIpaCode, clientSecret);
		Assertions.assertSame(storedClient, result);
	}

}
