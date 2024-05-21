package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;
import openapi.pagopa.payhub.model.AuthErrorDTO;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final String code;
    private final boolean printStackTrace;
    public InvalidTokenException(String message) {
        this(AuthErrorDTO.CodeEnum.INVALID_TOKEN.getValue(), message);
    }

    public InvalidTokenException(String code, String message) {
        this(code, message, false, null);
    }

    public InvalidTokenException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(message, ex);
        this.code = code;
        this.printStackTrace = printStackTrace;
    }
}
