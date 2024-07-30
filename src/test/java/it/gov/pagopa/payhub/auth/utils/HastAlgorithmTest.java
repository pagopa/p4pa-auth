package it.gov.pagopa.payhub.auth.utils;

import java.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashAlgorithmTest {

  private HashAlgorithm hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getDecoder().decode("PEPPER"));

  @Test
  void givenTextWhenHashThenOk() {
    // Given
    String plain = "PLAINTEXT";

    // When
    byte[] hash = hashAlgorithm.apply(plain);

    // Then
    Assertions.assertEquals("s+QUCtO7vYNzHCDrH03EVRGPZTyfIXwBKTRrgYWqwc4=", Base64.getEncoder().encodeToString(hash));
  }

  @Test
  void givenInvalidAlgorithmWhenHashThenNoSuchAlgorithmException() {
    //Given
    hashAlgorithm = new HashAlgorithm("invalidAlgorithm", Base64.getDecoder().decode("PEPPER"));
    //Then
    Assertions.assertThrows(IllegalStateException.class, () -> hashAlgorithm.apply("TEXT"));
  }
}