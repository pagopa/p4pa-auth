package it.gov.pagopa.payhub.auth.utils;

import java.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashAlgorithmTest {

  private final HashAlgorithm hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getDecoder().decode("PEPPER"));

  @Test
  void test() {
    // Given
    String plain = "PLAINTEXT";

    // When
    byte[] hash = hashAlgorithm.apply(plain);

    // Then
    Assertions.assertEquals("s+QUCtO7vYNzHCDrH03EVRGPZTyfIXwBKTRrgYWqwc4=", Base64.getEncoder().encodeToString(hash));
  }
}