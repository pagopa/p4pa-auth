package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.m2m.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.m2m.retrieve.ClientRetrieverService;
import it.gov.pagopa.payhub.auth.service.m2m.revoke.ClientRemovalService;
import it.gov.pagopa.payhub.dto.generated.ClientDTO;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	@Mock
	private ClientRemovalService clientRemovalServiceMock;

	@Mock
	private ClientRegistrationService clientRegistrationServiceMock;

	@Mock
	private ClientRetrieverService clientRetrieverServiceMock;

	@Mock
	private ClientMapper clientMapperMock;

	private ClientService service;

	@BeforeEach
	void init(){
		service = new ClientServiceImpl(clientRemovalServiceMock, clientRegistrationServiceMock, clientRetrieverServiceMock, clientMapperMock);
	}

	@AfterEach
	void verifyNotMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
			clientRemovalServiceMock,
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
		assertEquals(expectedClientDTO, actualClientDTO);
	}

	@Test
	void givenClientIdWhenGetClientThenReturnClient() {
		String organizationIpaCode = "organizationIpaCode";
		String clientId = "clientId";

		Client expectedClient = Client.builder()
				.clientId(clientId)
				.clientName("Test Client")
				.organizationIpaCode(organizationIpaCode)
				.clientSecret("secret".getBytes())
				.build();

		Mockito.when(clientRetrieverServiceMock.getClient(organizationIpaCode, clientId))
				.thenReturn(Optional.of(expectedClient));

		Optional<Client> result = service.getClient(organizationIpaCode, clientId);

		assertTrue(result.isPresent());
		assertEquals(expectedClient, result.get());
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
		assertEquals(List.of(dto1, dto2), result);
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
		assertEquals(Optional.of(expectedClient), result);
	}

	@Test
	void givenClientIdWhenRevokeClientThenVerifyRevoke() {
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientId = "clientId";
		//When
		service.revokeClient(organizationIpaCode, clientId);
		//Then
		Mockito.verify(clientRemovalServiceMock).revokeClient(organizationIpaCode, clientId);
	}
}
