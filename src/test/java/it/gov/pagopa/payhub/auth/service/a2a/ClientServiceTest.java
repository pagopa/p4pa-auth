package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retrieve.ClientRetrieverService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	@Mock
	private ClientRegistrationService clientRegistrationServiceMock;

	@Mock
	private ClientRetrieverService clientRetrieverServiceMock;

	@Mock
	private ClientMapper clientMapperMock;

	private ClientService service;

	@BeforeEach
	void init(){
		service = new ClientServiceImpl(clientRegistrationServiceMock, clientRetrieverServiceMock, clientMapperMock);
	}

	@AfterEach
	void verifyNotMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
			clientRegistrationServiceMock,
			clientRetrieverServiceMock,
			clientMapperMock
		);
	}

	@Test
	void whenRegisterClientThenIInvokeClientRegistrationService() {
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
		String clientSecretMock = UUID.randomUUID().toString();

		Mockito.when(clientRetrieverServiceMock.getClientSecret(organizationIpaCode, clientId)).thenReturn(clientSecretMock);
		//When
		String clientSecret = service.getClientSecret(organizationIpaCode, clientId);
		// Then
		Assertions.assertEquals(clientSecretMock, clientSecret);
	}

	@Test
	void givenOrganizationIpaCodeWhenGetClientsThenGetClientNoSecretDTOList() {
		//Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName1 = "SERVICE_001";
		String clientName2 = "SERVICE_002";

		ClientNoSecretDTO dto1 = ClientNoSecretDTO.builder()
			.organizationIpaCode(organizationIpaCode)
			.clientName(clientName1)
			.clientId(organizationIpaCode + clientName1)
			.build();
		ClientNoSecretDTO dto2 = ClientNoSecretDTO.builder()
			.organizationIpaCode(organizationIpaCode)
			.clientName(clientName2)
			.clientId(organizationIpaCode + clientName2)
			.build();

		Mockito.doReturn(List.of(dto1, dto2)).when(clientRetrieverServiceMock).getClients(organizationIpaCode);

		//When
		List<ClientNoSecretDTO> result = service.getClients(organizationIpaCode);
		//Then
		Assertions.assertEquals(List.of(dto1, dto2), result);
	}

	@Test
	void givenClientIdWhenGetClientByClientIdThenInvokeClientService() {
		// Given
		String clientId = "clientId";
		Client expectedClient = new Client();

		Mockito.when(clientRetrieverServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(expectedClient));
		//When
		Optional<Client> result = service.getClientByClientId(clientId);
		// Then
		Assertions.assertEquals(Optional.of(expectedClient), result);
	}

	@Test
	void givenRightCredentialsWhenVerifyCredentialsThenOk() {
		// Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName = "SERVICE_001";
		String clientId = organizationIpaCode + clientName;
		String clientSecretMock = UUID.randomUUID().toString();

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(clientSecretMock)
			.build();

		Mockito.when(clientRetrieverServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(mockClient));
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);
		// When
		ClientDTO actualClientDTO = service.authorizeCredentials(clientId, clientSecretMock);
		// Then
		Assertions.assertEquals(expectedClientDTO, actualClientDTO);
	}

	@Test
	void givenUnexpectedClientIdCredentialsWhenVerifyCredentialsThenClientUnauthorizedException() {
		// Given
		String clientId = "UNEXPECTED_CLIENT_ID";
		String clientSecretMock = UUID.randomUUID().toString();

		Mockito.when(clientRetrieverServiceMock.getClientByClientId(clientId)).thenThrow(new ClientUnauthorizedException("error"));
		// When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, clientSecretMock));
	}

	@Test
	void givenUnexpectedClientSecretCredentialsWhenVerifyCredentialsThenClientUnauthorizedException() {
		// Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName = "SERVICE_001";
		String clientId = organizationIpaCode + clientName;
		String clientSecret = UUID.randomUUID().toString();

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(UUID.randomUUID().toString())
			.build();

		Mockito.when(clientRetrieverServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(mockClient));
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);

		// When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, clientSecret));
	}
}
