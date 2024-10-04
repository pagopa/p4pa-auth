package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ClientRetrieverServiceTest {
	@Mock
	private ClientRepository clientRepositoryMock;

	@InjectMocks
	private ClientRetrieverService service;

	@Test
	void whenGetClientIdThenReturnStoredClientSecret(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;
		byte[] encryptedClientSecret = RandomUtils.nextBytes(16);
		Client storedClient = new Client(clientId, clientName, organizationIpaCode, encryptedClientSecret);

		Mockito.when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(storedClient));

		// When
		byte[] result = service.getClientSecret(organizationIpaCode, clientId);

		// Then
		Assertions.assertSame(encryptedClientSecret, result);
	}

}
