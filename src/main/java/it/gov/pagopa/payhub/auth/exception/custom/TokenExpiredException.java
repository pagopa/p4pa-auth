package it.gov.pagopa.payhub.auth.exception.custom;

import lombok.Getter;
import org.openapi.example.model.AuthErrorDTO;

@Getter
public class TokenExpiredException extends RuntimeException {
    private final String code;
    private final boolean printStackTrace;
    public TokenExpiredException(String message) {
        this(AuthErrorDTO.CodeEnum.TOKEN_EXPIRED_DATE.getValue(), message);
    }

    public TokenExpiredException(String code, String message) {
        this(code, message, false, null);
    }

    public TokenExpiredException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(message, ex);
        this.code = code;
        this.printStackTrace = printStackTrace;
    }
}
