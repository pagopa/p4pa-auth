package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.service.DataCipherService;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class ExternalUserIdObfuscatorService {

  private final DataCipherService dataCipherService;

  public ExternalUserIdObfuscatorService(DataCipherService dataCipherService) {
    this.dataCipherService = dataCipherService;
  }

  public String obfuscate(String externalUserId) {
    return Base64.getEncoder().encodeToString(dataCipherService.hash(externalUserId));
  }
}
