package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.utils.AESUtils;
import it.gov.pagopa.payhub.auth.utils.HashAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Base64;

@Service
public class DataCipherService {

  private final String encryptPsw;
  private final HashAlgorithm hashAlgorithm;
  private final JsonMapper jsonMapper;

  public DataCipherService(
    @Value("${data-cipher.p4pa-auth-encrypt-psw}") String encryptPsw,
    @Value("${data-cipher.p4pa-auth-hash-key}") String hashPepper,
    JsonMapper jsonMapper
  ) {
    this.encryptPsw = encryptPsw;
    this.jsonMapper = jsonMapper;

    hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getDecoder().decode(hashPepper));
  }

  public byte[] encrypt(String plainText){
    return AESUtils.encrypt(encryptPsw, plainText);
  }

  public String decrypt(byte[] cipherData){
    return AESUtils.decrypt(encryptPsw, cipherData);
  }

  public <T> byte[] encryptObj(T obj){
    try {
      return encrypt(jsonMapper.writeValueAsString(obj));
    } catch (JacksonException e) {
      throw new IllegalStateException("[JSON_SERIALIZATION_ERROR] Cannot serialize object as JSON", e);
    }
  }

  public <T> T decryptObj(byte[] cipherData, Class<T> clazz) {
    try {
      return jsonMapper.readValue(decrypt(cipherData), clazz);
    } catch (JacksonException e) {
      throw new IllegalStateException("[JSON_DESERIALIZATION_ERROR] Cannot deserialize object as JSON", e);
    }
  }

  public byte[] hash(String value) {
    return hashAlgorithm.apply(value);
  }
}
