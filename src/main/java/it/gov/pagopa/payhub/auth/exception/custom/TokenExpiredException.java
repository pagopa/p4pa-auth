package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
