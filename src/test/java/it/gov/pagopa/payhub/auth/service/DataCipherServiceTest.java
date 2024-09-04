package it.gov.pagopa.payhub.auth.service;


import java.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataCipherServiceTest {

  private final DataCipherService service = new DataCipherService("PEPPER");

  @Test
  void givenTextWhenHashThenOk() {
    // Given
    String plain = "PLAINTEXT";

    // When
    byte[] hash = service.hash(plain);

    // Then
    Assertions.assertEquals("s-QUCtO7vYNzHCDrH03EVRGPZTyfIXwBKTRrgYWqwc4=", Base64.getUrlEncoder().encodeToString(hash));
  }
}