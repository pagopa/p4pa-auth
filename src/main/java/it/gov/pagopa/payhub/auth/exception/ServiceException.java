package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

  private final AuthErrorDTO.CodeEnum code;

  public ServiceException(AuthErrorDTO.CodeEnum code, String message) {
    super(message);
    this.code = code;
  }

}
