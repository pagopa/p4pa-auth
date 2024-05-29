package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidTokenIssuerException extends RuntimeException {

    public InvalidTokenIssuerException(String message) {
        super(message);
    }
}
