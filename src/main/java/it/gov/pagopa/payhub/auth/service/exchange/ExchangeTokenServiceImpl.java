package it.gov.pagopa.payhub.auth.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExchangeTokenServiceImpl implements ExchangeTokenService{

    private final ValidateTokenService validateTokenService;

    public ExchangeTokenServiceImpl(ValidateTokenService validateTokenService) {
        this.validateTokenService = validateTokenService;
    }

    @Override
    public void postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        log.info("Client {} requested to exchange a {} token provided by {} asking for grant type {} and scope {}",
                clientId, subjectTokenType, subjectIssuer, grantType, scope);
        validateTokenService.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }
}
