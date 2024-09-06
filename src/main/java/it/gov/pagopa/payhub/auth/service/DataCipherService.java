package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.utils.HashAlgorithm;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DataCipherService {

  private final HashAlgorithm hashAlgorithm;

  public DataCipherService(@Value("${data-cipher.p4pa-auth-hash-key}") String hashPepper) {
    hashAlgorithm = new HashAlgorithm("SHA-256", Base64.getUrlDecoder().decode(hashPepper));
  }

  public byte[] hash(String value) {
    return hashAlgorithm.apply(value);
  }
}
