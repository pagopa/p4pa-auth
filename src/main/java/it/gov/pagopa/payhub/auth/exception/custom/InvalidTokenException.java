package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.ServiceException;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import lombok.Getter;

@Getter
public class InvalidTokenException extends ServiceException {
    public InvalidTokenException(String message) {
        this(AuthErrorDTO.CodeEnum.INVALID_TOKEN, message);
    }

    public InvalidTokenException(AuthErrorDTO.CodeEnum code, String message) {
        super(code, message);
    }
}
