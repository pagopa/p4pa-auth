package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import it.gov.pagopa.payhub.model.generated.CreateClientRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

	@Mock
	private ClientRegistrationService clientRegistrationServiceMock;

	@Mock
	private ClientMapper clientMapper;

	private ClientService service;

	@BeforeEach
	void init(){
		service = new ClientServiceImpl(clientRegistrationServiceMock, clientMapper);
	}

	@AfterEach
	void verifyNotMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
			clientRegistrationServiceMock,
			clientMapper
		);
	}

	@Test
	void whenCreateClientThenVerifyClient() {
		String organizationIpaCode = "organizationIpaCode";
		String clientId = "clientId";

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = new ClientDTO();

		Mockito.when(clientRegistrationServiceMock.registerClient(clientId, organizationIpaCode)).thenReturn(mockClient);

		Mockito.when(clientMapper.mapToDTO(mockClient)).thenReturn(expectedClientDTO);

		ClientDTO actualClientDTO = service.registerClient(clientId, organizationIpaCode);

		Assertions.assertEquals(expectedClientDTO, actualClientDTO);
	}
}
