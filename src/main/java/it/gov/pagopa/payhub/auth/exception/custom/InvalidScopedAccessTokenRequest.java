package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;

@Getter
public class InvalidScopedAccessTokenRequest extends RuntimeException {

    public InvalidScopedAccessTokenRequest(String message) {
        super(message);
    }
}
