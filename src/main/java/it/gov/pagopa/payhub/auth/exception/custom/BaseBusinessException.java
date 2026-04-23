package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public abstract class BaseBusinessException extends RuntimeException {

  protected final String code;

  protected BaseBusinessException(String code, String message) {
    this(code, message, null);
  }

  protected BaseBusinessException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}
