package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

  @Mock
  private DataCipherService dataCipherService;

  @InjectMocks
  private ClientMapper service;

  private String uuidForSecret = UUID.randomUUID().toString();

  @Test
  void givenClientWhenMapThenGetClientDTO() {
    // Given
    byte[] chiper = dataCipherService.encrypt(uuidForSecret);
    Client client = Client.builder()
      .clientId("clientId")
      .organizationIpaCode("ipa_code")
      .clientSecret(chiper)
      .build();

    // When
    ClientDTO result = service.mapToDTO(client);

    // Then
    Assertions.assertEquals(
      ClientDTO.builder()
        .clientId("clientId")
        .organizationIpaCode("ipa_code")
        .clientSecret(dataCipherService.decrypt(chiper))
        .build(), result
    );
  }

  @Test
  void givenClientDTOWhenMapThenGetClient() {
    // Given
    byte[] chiper = dataCipherService.encrypt(uuidForSecret);
    ClientDTO clientDTO = ClientDTO.builder()
      .clientId("clientId")
      .organizationIpaCode("ipa_code")
      .clientSecret(dataCipherService.decrypt(chiper))
      .build();

      // When
    Client result = service.mapToModel(clientDTO);

    // Then
    Assertions.assertEquals(
      Client.builder()
        .clientId("clientId")
        .organizationIpaCode("ipa_code")
        .clientSecret(chiper)
        .build(), result
    );
  }
}