package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidGrantTypeException extends RuntimeException {

    public InvalidGrantTypeException(String message) {
        super(message);
    }
}
