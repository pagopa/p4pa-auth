package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
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
	void whenCreateClientThenVerifyClient() {
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
}
