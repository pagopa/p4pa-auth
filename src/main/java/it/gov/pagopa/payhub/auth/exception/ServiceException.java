package it.gov.pagopa.payhub.auth.exception;

import lombok.Getter;
import openapi.pagopa.payhub.model.AuthErrorDTO;

@Getter
public class ServiceException extends RuntimeException {
  private final AuthErrorDTO.CodeEnum code;
  private final boolean printStackTrace;

  public ServiceException(AuthErrorDTO.CodeEnum code, String message) {
    this(code, message, false, null);
  }

  public ServiceException(AuthErrorDTO.CodeEnum code, String message, boolean printStackTrace, Throwable ex) {
    super(message, ex);
    this.code = code;
    this.printStackTrace = printStackTrace;
  }
}
