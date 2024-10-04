package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private ClientRegistrationService service;

    @Test
    void whenRegisterClientThenReturnStoredClient(){
        // Given
        String organizationIpaCode = "organizationIpaCode";
        String clientName = "clientName";
        String clientId = organizationIpaCode + clientName;
        String uuidForClientSecret = UUID.randomUUID().toString();

        ClientDTO clientDTO = ClientDTO.builder()
          .clientId(clientId)
          .clientName(clientName)
          .organizationIpaCode(organizationIpaCode)
          .clientSecret(uuidForClientSecret)
          .build();

        Client clientMapped = clientMapperMock.mapToModel(clientDTO);
        Client storedClient = new Client();

        Mockito.when(clientRepositoryMock.insert(clientMapped)).thenReturn(storedClient);

        // When
        Client result = service.registerClient(clientName, organizationIpaCode);

        // Then
        Assertions.assertSame(storedClient, result);
    }
}
