package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retrieve.ClientRetrieverService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	@Mock
	private ClientRegistrationService clientRegistrationServiceMock;

	@Mock
	private ClientRetrieverService clientRetrieverServiceMock;

	@Mock
	private DataCipherService dataCipherServiceMock;

	@Mock
	private ClientMapper clientMapperMock;

	private ClientService service;

	@BeforeEach
	void init(){
		service = new ClientServiceImpl(clientRegistrationServiceMock, clientRetrieverServiceMock, dataCipherServiceMock, clientMapperMock);
	}

	@AfterEach
	void verifyNotMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
			clientRegistrationServiceMock,
			clientRetrieverServiceMock,
			dataCipherServiceMock,
			clientMapperMock
		);
	}

	@Test
	void whenRegisterClientThenInvokeClientRegistrationService() {
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = new ClientDTO();

		Mockito.when(clientRegistrationServiceMock.registerClient(clientName, organizationIpaCode)).thenReturn(mockClient);
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);
		// When
		ClientDTO actualClientDTO = service.registerClient(clientName, organizationIpaCode);
		// Then
		Assertions.assertEquals(expectedClientDTO, actualClientDTO);
	}

	@Test
	void givenClientIdWhenGetEncryptedClientSecretThenGetClientSecret() {
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientId = "clientId";
		byte[] encryptedClientSecret = new byte[16];
		new Random().nextBytes(encryptedClientSecret);
		String clientSecretMock = UUID.randomUUID().toString();

		Mockito.doReturn(encryptedClientSecret).when(clientRetrieverServiceMock).getClientSecret(organizationIpaCode, clientId);
		Mockito.when(dataCipherServiceMock.decrypt(encryptedClientSecret)).thenReturn(clientSecretMock);
		//When
		String clientSecret = service.getClientSecret(organizationIpaCode, clientId);
		// Then
		Assertions.assertEquals(clientSecretMock, clientSecret);
	}

	@Test
	void givenOrganizationIpaCodeWhenGetClientsThenEmptyList() {
		//Given
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

		Mockito.doReturn(List.of(c1, c2)).when(clientRetrieverServiceMock).getClients(organizationIpaCode);
		ClientDTO expectedDto1 = new ClientDTO();
		ClientDTO expectedDto2 = new ClientDTO();
		Mockito.when(clientMapperMock.mapToDTO(c1)).thenReturn(expectedDto1);
		Mockito.when(clientMapperMock.mapToDTO(c2)).thenReturn(expectedDto2);

		//When
		List<ClientDTO> result = service.getClients(organizationIpaCode);
		//Then
		Assertions.assertEquals(List.of(expectedDto1, expectedDto2), result);
	}

}
