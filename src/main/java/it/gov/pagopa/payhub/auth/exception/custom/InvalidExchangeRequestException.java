package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidExchangeRequestException extends BaseBusinessException {

    public InvalidExchangeRequestException(String code, String message) {
        super(code, message);
    }
}
