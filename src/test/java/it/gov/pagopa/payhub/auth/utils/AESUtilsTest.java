package it.gov.pagopa.payhub.auth.utils;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Base64;

import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

public class AESUtilsTest {

	@Test
	void whenDecryptThenVerifyDecrypt() {
		try (MockedStatic<AESUtils> mocked = mockStatic(AESUtils.class, CALLS_REAL_METHODS)) {
			// Given
			String encodeToString = "7sPyx/lKfEGir6eGiS16zkWwsynNJofA151pg0jZt0U+6A0+P5WBLjuMaBdYUExUzcj5Pl0=";
			String psw = "PSW";
			byte[] decode = Base64.getDecoder().decode(encodeToString);
			String result ="";
			// When
			mocked.when(() -> AESUtils.decrypt(psw, decode)).thenReturn(null);
			// Verify
			mocked.verify(() -> AESUtils.decrypt(psw, decode));
		}
	}

	@Test
	void whenEncryptThenVerifyEncrypt() {
		try (MockedStatic<AESUtils> mocked = mockStatic(AESUtils.class, CALLS_REAL_METHODS)) {
			// Given
			String plain = "PLAINTEXT";
			String psw = "PSW";
			// When
			mocked.when(() -> AESUtils.encrypt(psw, plain)).thenReturn(null);
			// Verify
			mocked.verify(() -> AESUtils.encrypt(psw, plain));
		}
	}

}
