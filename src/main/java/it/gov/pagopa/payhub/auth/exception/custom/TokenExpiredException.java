package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.ServiceException;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import lombok.Getter;

@Getter
public class TokenExpiredException extends ServiceException {
    public TokenExpiredException(String message) {
        this(AuthErrorDTO.CodeEnum.TOKEN_EXPIRED_DATE, message);
    }

    public TokenExpiredException(AuthErrorDTO.CodeEnum code, String message) {
        this(code, message, false, null);
    }

    public TokenExpiredException(AuthErrorDTO.CodeEnum code, String message, boolean printStackTrace, Throwable ex) {
        super(code, message, printStackTrace, ex);
    }
}
