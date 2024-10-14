package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import it.gov.pagopa.payhub.model.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

  @Mock
  private DataCipherService dataCipherServiceMock;

  private ClientMapper service;

  @BeforeEach
  void init(){
    service = new ClientMapper(dataCipherServiceMock);
  }

  @Test
  void WhenMapThenGetClientMapped() {
    // Given
    String plainClientSecret = UUID.randomUUID().toString();
    String organizationIpaCode = "organizationIpaCode";
    String clientName = "clientName";
    String clientId = organizationIpaCode + clientName;
    byte[] encryptedClientSecretMock = new byte[0];

    Client client = Client.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(encryptedClientSecretMock)
      .build();

    ClientDTO clientDTO = ClientDTO.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(plainClientSecret)
      .build();

    Mockito.when(dataCipherServiceMock.encrypt(plainClientSecret)).thenReturn(encryptedClientSecretMock);
    // When
    Client modelMapped = service.mapToModel(clientDTO);

    // Then
    Assertions.assertEquals(client, modelMapped);
  }

  @Test
  void WhenMapThenGetClientDTOMapped() {
    // Given
    byte[] encryptedClientSecret = new byte[16];
    new Random().nextBytes(encryptedClientSecret);
    String decryptedClientSecret = UUID.randomUUID().toString();
    String organizationIpaCode = "organizationIpaCode";
    String clientName = "clientName";
    String clientId = organizationIpaCode + clientName;

    ClientDTO clientDTO = ClientDTO.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(decryptedClientSecret)
      .build();

    Client client = Client.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(encryptedClientSecret)
      .build();

    Mockito.when(dataCipherServiceMock.decrypt(encryptedClientSecret)).thenReturn(decryptedClientSecret);

    // When
    ClientDTO dtoMapped = service.mapToDTO(client);
    // Then
    Assertions.assertEquals(clientDTO, dtoMapped);
  }

  @Test
  void WhenMapThenGetClientNoSecretDTOMapped() {
    // Given
    byte[] encryptedClientSecret = new byte[16];
    new Random().nextBytes(encryptedClientSecret);
    String decryptedClientSecret = UUID.randomUUID().toString();
    String organizationIpaCode = "organizationIpaCode";
    String clientName = "clientName";
    String clientId = organizationIpaCode + clientName;

    ClientNoSecretDTO clientDTO = ClientNoSecretDTO.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .build();

    Client client = Client.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(encryptedClientSecret)
      .build();

    Mockito.when(dataCipherServiceMock.decrypt(encryptedClientSecret)).thenReturn(decryptedClientSecret);

    // When
    ClientNoSecretDTO dtoMapped = service.mapToNoSecretDTO(client);
    // Then
    Assertions.assertEquals(clientDTO, dtoMapped);
  }

}
