package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

  @Mock
  private DataCipherService dataCipherService;

  private ClientMapper service;

  @BeforeEach
  void init(){
    service = new ClientMapper(dataCipherService);
  }

  @Test
  void WhenMapThenGetClientMapped() {
    // Given
    String organizationIpaCode = "organizationIpaCode";
    String clientName = "clientName";
    String clientId = organizationIpaCode + clientName;
    byte[] encryptedClientSecretMock = Mockito.anyString().getBytes(StandardCharsets.UTF_8);

    Client client = Client.builder()
      .clientId(clientId)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(encryptedClientSecretMock)
      .build();
    Mockito.when(dataCipherService.encrypt(UUID.randomUUID().toString())).thenReturn(encryptedClientSecretMock);
    // When
    Client modelMapped = service.mapToModel(clientId, clientName, organizationIpaCode, Mockito.anyString());

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

    Mockito.when(dataCipherService.decrypt(encryptedClientSecret)).thenReturn(decryptedClientSecret);

    // When
    ClientDTO dtoMapped = service.mapToDTO(clientId, clientName, organizationIpaCode, encryptedClientSecret);
    // Then
    Assertions.assertEquals(clientDTO, dtoMapped);
  }

}
