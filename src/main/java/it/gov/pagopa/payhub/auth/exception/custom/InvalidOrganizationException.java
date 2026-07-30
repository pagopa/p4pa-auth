package it.gov.pagopa.payhub.auth.exception.custom;

public class InvalidOrganizationException extends BaseBusinessException {

  public InvalidOrganizationException(String code, String message) {
    super(code, message);
  }
}

