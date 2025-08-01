package it.gov.pagopa.payhub.auth.service.m2m.retrieve;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ClientRetrieverServiceTest {
	@Mock
	private ClientRepository clientRepositoryMock;

	@Mock
	private ClientMapper clientMapperMock;

	@InjectMocks
	private ClientRetrieverService service;

	@Test
	void givenValidClientIdAndMatchingOrganizationIpaCodeWhenGetClientThenReturnClient() {
		String organizationIpaCode = "ORG123";
		String clientId = "client-abc";
		Client client = Client.builder()
				.clientId(clientId)
				.clientName("MyClient")
				.organizationIpaCode(organizationIpaCode)
				.clientSecret("secret".getBytes())
				.build();

		Mockito.when(clientRepositoryMock.findById(clientId))
				.thenReturn(Optional.of(client));

		Optional<Client> result = service.getClient(organizationIpaCode, clientId);

		assertTrue(result.isPresent());
		assertEquals(client, result.get());
	}

	@Test
	void givenValidClientIdButMismatchedOrganizationIpaCodeWhenGetClientThenReturnEmpty() {
		String organizationIpaCode = "ORG123";
		String clientId = "client-abc";
		Client client = Client.builder()
				.clientId(clientId)
				.clientName("MyClient")
				.organizationIpaCode("DIFFERENT_ORG")
				.clientSecret("secret".getBytes())
				.build();

		Mockito.when(clientRepositoryMock.findById(clientId))
				.thenReturn(Optional.of(client));

		Optional<Client> result = service.getClient(organizationIpaCode, clientId);

		assertTrue(result.isEmpty());
	}

	@Test
	void givenNonExistingClientIdWhenGetClientThenReturnEmpty() {
		String organizationIpaCode = "ORG123";
		String clientId = "client-xyz";

		Mockito.when(clientRepositoryMock.findById(clientId))
				.thenReturn(Optional.empty());

		Optional<Client> result = service.getClient(organizationIpaCode, clientId);

		assertTrue(result.isEmpty());
	}

	@Test
	void givenOrganizationIpaCodeWhenGetClientsThenInvokeClientRetrieverService(){
		// Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName1 = "SERVICE_001";
		String clientName2 = "SERVICE_002";
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);

		Client c1 = Client.builder()
			.organizationIpaCode(organizationIpaCode)
			.clientName(clientName1)
			.clientId(organizationIpaCode + clientName1)
			.clientSecret(encryptedClientSecret)
			.build();
		Client c2 = Client.builder()
			.organizationIpaCode(organizationIpaCode)
			.clientName(clientName2)
			.clientId(organizationIpaCode + clientName2)
			.clientSecret(encryptedClientSecret)
			.build();

		Mockito.doReturn(List.of(c1, c2)).when(clientRepositoryMock).findAllByOrganizationIpaCode(organizationIpaCode);
		ClientNoSecretDTO expectedDto1 = new ClientNoSecretDTO();
		ClientNoSecretDTO expectedDto2 = new ClientNoSecretDTO();
		Mockito.when(clientMapperMock.mapToNoSecretDTO(c1)).thenReturn(expectedDto1);
		Mockito.when(clientMapperMock.mapToNoSecretDTO(c2)).thenReturn(expectedDto2);

		// When
		List<ClientNoSecretDTO> result = service.getClients(organizationIpaCode);

		// Then
		assertEquals(List.of(expectedDto1, expectedDto2), result);
	}

	@Test
	void givenClientIdWhenFindByIdThenInvokeClientRetrieverService(){
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);
		Client storedClient = new Client(clientId, clientName, organizationIpaCode, encryptedClientSecret);

		Mockito.when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(storedClient));

		// When
		Optional<Client> result = service.getClientByClientId(clientId);

		// Then
		assertEquals(Optional.of(storedClient), result);
	}
}
