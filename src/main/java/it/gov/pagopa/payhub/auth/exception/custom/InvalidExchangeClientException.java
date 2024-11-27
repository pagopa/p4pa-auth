package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidExchangeClientException extends RuntimeException {

    public InvalidExchangeClientException(String message) {
        super(message);
    }
}
