package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class ClientRetrieverServiceTest {
	@Mock
	private ClientRepository clientRepositoryMock;

	@Mock
	private DataCipherService dataCipherServiceMock;

	@Mock
	private ClientMapper clientMapperMock;

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
		Mockito.when(dataCipherServiceMock.decrypt(encryptedClientSecret)).thenReturn(expectedClientSecretPlain);

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
		Assertions.assertEquals(List.of(expectedDto1, expectedDto2), result);
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
		Assertions.assertEquals(Optional.of(storedClient), result);
	}
}
