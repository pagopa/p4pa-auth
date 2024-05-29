package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidExchangeRequestException extends RuntimeException {

    public InvalidExchangeRequestException(String message) {
        super(message);
    }
}
