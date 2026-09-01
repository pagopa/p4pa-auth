package it.gov.pagopa.payhub.auth.exception.custom;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import lombok.Getter;

@Getter
public class InvalidExchangeClientException extends BaseBusinessException {

    public InvalidExchangeClientException(String code, String message) {
        super(code, message);
    }
}
