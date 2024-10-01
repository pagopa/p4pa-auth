package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientRetrieverService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
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
	private ClientRetrieverService clientRetrieverServiceMock;

	@Mock
	private DataCipherService dataCipherServiceMock;

	@Mock
	private ClientMapper clientMapper;

	private ClientService service;

	@BeforeEach
	void init(){
		service = new ClientServiceImpl(clientRegistrationServiceMock, clientRetrieverServiceMock, dataCipherServiceMock, clientMapper);
	}

	@AfterEach
	void verifyNotMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
			clientRegistrationServiceMock,
			clientRetrieverServiceMock,
			dataCipherServiceMock,
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
