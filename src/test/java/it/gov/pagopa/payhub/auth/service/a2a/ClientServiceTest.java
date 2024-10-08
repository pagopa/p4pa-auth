package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
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
}
