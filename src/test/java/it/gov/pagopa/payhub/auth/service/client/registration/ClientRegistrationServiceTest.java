package it.gov.pagopa.payhub.auth.service.client.registration;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.service.a2a.registration.ClientRegistrationService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
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
class ClientRegistrationServiceTest {

    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepositoryMock;

    private ClientRegistrationService service;


    @BeforeEach
    void init(){
        service = new ClientRegistrationService(
          clientMapper,
          clientRepositoryMock
        );
    }

    @AfterEach
    void verifyNotMoreInvocation(){
        Mockito.verifyNoMoreInteractions(
          clientMapper,
          clientRepositoryMock
        );
    }

    @Test
    void whenRegisterClientThenReturnStoredClient(){
        // Given
        String organizationIpaCode = "organizationIpaCode";
        String clientId = "clientId";
        String uuidForClientSecret = UUID.randomUUID().toString();

        Client client = clientMapper.mapToModel(clientId, organizationIpaCode, uuidForClientSecret);
        Client storedClient = new Client();

        Mockito.when(clientRepositoryMock.insert(client)).thenReturn(storedClient);

        // When
        Client result = service.registerClient(clientId, organizationIpaCode);

        // Then
        Assertions.assertSame(storedClient, result);
    }
}
