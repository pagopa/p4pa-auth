package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class ClientRetrieverServiceTest {
	@Mock
	private ClientRepository clientRepositoryMock;

	@Mock
	private DataCipherService dataCipherService;

	@InjectMocks
	private ClientRetrieverService service;

	@Test
	void givenGetClientIdWhenDecryptThenInvokeClientRetrieverService(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);
		Client storedClient = new Client(clientId, clientName, organizationIpaCode, encryptedClientSecret);
		String expectedClientSecretPlain = "expectedClientSecretPlain";

		Mockito.when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(storedClient));
		Mockito.when(dataCipherService.decrypt(encryptedClientSecret)).thenReturn(expectedClientSecretPlain);

		// When
		String result = service.getClientSecret(organizationIpaCode, clientId);

		// Then
		Assertions.assertSame(expectedClientSecretPlain, result);
	}

	@Test
	void givenNotClientIdWhenDecryptThenClientNotFoundException(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);
		Client storedClient = new Client(clientId, clientName, organizationIpaCode, encryptedClientSecret);

		Mockito.when(clientRepositoryMock.findById(storedClient.getClientId())).thenReturn(Optional.empty());

		// When, Then
		Assertions.assertThrows(ClientNotFoundException.class, () -> service.getClientSecret(organizationIpaCode, clientId));
	}

}
