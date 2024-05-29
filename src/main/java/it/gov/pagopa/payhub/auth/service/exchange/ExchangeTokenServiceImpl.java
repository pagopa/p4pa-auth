package it.gov.pagopa.payhub.auth.service.exchange;

import org.springframework.stereotype.Service;

@Service
public class ExchangeTokenServiceImpl implements ExchangeTokenService{

    private final ValidateTokenService validateTokenService;

    public ExchangeTokenServiceImpl(ValidateTokenService validateTokenService) {
        this.validateTokenService = validateTokenService;
    }

    @Override
    public void postToken(String token) {
        validateTokenService.validate(token);
    }
}
