package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class ClientRetrieverServiceTest {
	@Mock
	private ClientRepository clientRepositoryMock;

	@InjectMocks
	private ClientRetrieverService service;

	@Test
	void givenGetClientIdWhenReturnStoredClientSecretThenInvokeClientRetrieverService(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);
		Client storedClient = new Client(clientId, clientName, organizationIpaCode, encryptedClientSecret);

		Mockito.when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(storedClient));

		// When
		byte[] result = service.getClientSecret(organizationIpaCode, clientId);

		// Then
		Assertions.assertSame(encryptedClientSecret, result);
	}

	@Test
	void givenOrganizationIpaCodeWhenGetClientsThenInvokeClientRetrieverService(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		List<Client> storedClients = new ArrayList<>();

		Mockito.when(clientRepositoryMock.findAllByOrganizationIpaCode(organizationIpaCode)).thenReturn(storedClients);

		// When
		List<Client> result = service.getClients(organizationIpaCode);

		// Then
		Assertions.assertSame(storedClients, result);
	}

}
