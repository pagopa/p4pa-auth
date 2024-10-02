package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
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
    private ClientMapper clientMapperMock;
    @Mock
    private ClientRepository clientRepositoryMock;

    private ClientRegistrationService service;


    @BeforeEach
    void init(){
        service = new ClientRegistrationService(
          clientMapperMock,
          clientRepositoryMock
        );
    }

    @AfterEach
    void verifyNotMoreInvocation(){
        Mockito.verifyNoMoreInteractions(
          clientMapperMock,
          clientRepositoryMock
        );
    }

    @Test
    void whenRegisterClientThenReturnStoredClient(){
        // Given
        String organizationIpaCode = "organizationIpaCode";
        String clientName = "clientName";
        String uuidForClientSecret = UUID.randomUUID().toString();

        Client client = clientMapperMock.mapToModel(clientName, organizationIpaCode, uuidForClientSecret);
        Client storedClient = new Client();

        Mockito.when(clientRepositoryMock.insert(client)).thenReturn(storedClient);

        // When
        Client result = service.registerClient(clientName, organizationIpaCode);

        // Then
        Assertions.assertSame(storedClient, result);
    }
}
