package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import lombok.Getter;

@Getter
public class InvalidExchangeRequestException extends BaseBusinessException {

    public InvalidExchangeRequestException(String code, String message) {
        super(code, message);
    }
}
