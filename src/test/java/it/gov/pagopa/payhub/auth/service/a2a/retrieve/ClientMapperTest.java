package it.gov.pagopa.payhub.auth.service.a2a.retrieve;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.service.DataCipherService;
import it.gov.pagopa.payhub.auth.service.a2a.retreive.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ClientMapperTest {

  private final DataCipherService dataCipherService = new DataCipherService("PSW","PEPPER", new ObjectMapper());

  private ClientMapper service;

  @BeforeEach
  void init(){
    service = new ClientMapper(dataCipherService);
  }

  @Test
  void givenClientDTOWhenMapThenGetClientDTOAgain() {
    // Given
    String uuidForSecret = UUID.randomUUID().toString();
    String organizationIpaCode = "organizationIpaCode";
    String clientName = "clientName";
    ClientDTO clientDTO = ClientDTO.builder()
      .clientId(organizationIpaCode+clientName)
      .clientName(clientName)
      .organizationIpaCode(organizationIpaCode)
      .clientSecret(uuidForSecret)
      .build();

    // When
    Client modelMapped = service.mapToModel(clientDTO);
    ClientDTO dtoMapped = service.mapToDTO(modelMapped);

    // Then
    Assertions.assertEquals(clientDTO, dtoMapped);
  }
}