package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.exception.custom.M2MClientConflictException;
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
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class ClientRegistrationServiceTest {

    @Mock
    private ClientMapper clientMapperMock;
    @Mock
    private ClientRepository clientRepositoryMock;

    @InjectMocks
    private ClientRegistrationService service;

    @Test
    void whenRegisterClientThenReturnStoredClient() {
        // Given
        String organizationIpaCode = "organizationIpaCode";
        String clientName = "clientName";

        Client mappedClient = configureClientMapperMock(organizationIpaCode, clientName);

        Client storedClient = new Client();
        Mockito.when(clientRepositoryMock.insert(mappedClient)).thenReturn(storedClient);

        // When
        Client result = service.registerClient(clientName, organizationIpaCode);

        // Then
        Assertions.assertSame(storedClient, result);
    }

    @Test
    void givenDuplicateClientIdWhenRegisterClientThenM2MClientConflictException() {
        String organizationIpaCode = "IPACODE";
        String clientName = "CLIENTNAME";

        Client mappedClient = configureClientMapperMock(organizationIpaCode, clientName);

        Mockito.when(clientRepositoryMock.insert(mappedClient))
                .thenThrow(new DuplicateKeyException(""));

        Assertions.assertThrows(M2MClientConflictException.class,
                () -> service.registerClient(clientName, organizationIpaCode)
        );
    }

    private Client configureClientMapperMock(String organizationIpaCode, String clientName) {
        Client mappedClient = new Client();
        Mockito.when(clientMapperMock.mapToModel(Mockito.argThat(c -> {
                    Assertions.assertNotNull(c.getClientSecret());
                    c.setClientSecret(null);

                    Assertions.assertEquals(
                            ClientDTO.builder()
                                    .clientId(organizationIpaCode + clientName)
                                    .clientName(clientName)
                                    .organizationIpaCode(organizationIpaCode)
                                    .build(),
                            c);
                    return true;
                })))
                .thenReturn(mappedClient);
        return mappedClient;
    }
}
