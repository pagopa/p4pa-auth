package it.gov.pagopa.payhub.auth.service.exchange;

import org.springframework.stereotype.Service;

@Service
public class ExchangeTokenServiceImpl implements ExchangeTokenService{

    private final ValidateTokenService validateTokenService;

    public ExchangeTokenServiceImpl(ValidateTokenService validateTokenService) {
        this.validateTokenService = validateTokenService;
    }

    @Override
    public void postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        validateTokenService.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }
}
