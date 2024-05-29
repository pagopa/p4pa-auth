package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{
    private final ExchangeTokenService exchangeTokenService;

    public AuthServiceImpl(ExchangeTokenService exchangeTokenService) {
        this.exchangeTokenService = exchangeTokenService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        return exchangeTokenService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }
}
