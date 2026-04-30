package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidExchangeClientException extends BaseBusinessException {

    public InvalidExchangeClientException(String code, String message) {
        super(code, message);
    }
}
