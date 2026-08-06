package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;

public class InvalidOrganizationException extends BaseBusinessException {

  public InvalidOrganizationException(String code, String message) {
    super(code, message);
  }
}

