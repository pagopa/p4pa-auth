package it.gov.pagopa.payhub.auth.utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESUtils {
	private AESUtils() {	}

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final String FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
	private static final int TAG_LENGTH_BIT = 128;
	private static final int IV_LENGTH_BYTE = 12;
	private static final int SALT_LENGTH_BYTE = 16;
	private static final String ALGORITHM_TYPE = "AES";
	private static final int KEY_LENGTH = 256;
	private static final int ITERATION_COUNT = 65536;
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	public static byte[] getRandomNonce(int length) {
		byte[] nonce = new byte[length];
		new SecureRandom().nextBytes(nonce);
		return nonce;
	}

	public static SecretKey getSecretKey(String password, byte[] salt) {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_INSTANCE);
			return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM_TYPE);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Cannot initialize cryptographic data", e);
		}
	}

	public static byte[] encrypt(String password, String plainMessage) {
		byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
		SecretKey secretKey = getSecretKey(password, salt);

		// GCM recommends 12 bytes iv
		byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
		Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

		byte[] encryptedMessageByte = executeCipherOp(cipher, plainMessage.getBytes(UTF_8));

		// prefix IV and Salt to cipher text
		return ByteBuffer.allocate(iv.length + salt.length + encryptedMessageByte.length)
			.put(iv)
			.put(salt)
			.put(encryptedMessageByte)
			.array();
	}

	public static String decrypt(String password, byte[] cipherMessage) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);

		byte[] iv = new byte[IV_LENGTH_BYTE];
		byteBuffer.get(iv);

		byte[] salt = new byte[SALT_LENGTH_BYTE];
		byteBuffer.get(salt);

		byte[] encryptedByte = new byte[byteBuffer.remaining()];
		byteBuffer.get(encryptedByte);

		SecretKey secretKey = getSecretKey(password, salt);
		Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] decryptedMessageByte = executeCipherOp(cipher, encryptedByte);
		return new String(decryptedMessageByte, UTF_8);
	}

	private static byte[] executeCipherOp(Cipher cipher, byte[] encryptedByte) {
		try {
			return cipher.doFinal(encryptedByte);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException("Cannot execute cipher op", e);
		}
	}

	private static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(mode, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
			return cipher;
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
		         | InvalidAlgorithmParameterException e) {
			throw new IllegalStateException("Cannot initialize cipher data", e);
		}
	}

}
