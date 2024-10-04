package it.gov.pagopa.payhub.auth.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AESUtilsTest {
	
	@Test
	void test() {
		// Given
		final String plainToEncrypt = "PLAINTEXT";
		final String passwordTest = "PSW";
		// When
		byte[] cipher = AESUtils.encrypt(passwordTest, plainToEncrypt);
		String result = AESUtils.decrypt(passwordTest, cipher);

		// Then
		Assertions.assertEquals(result, plainToEncrypt);
	}
}