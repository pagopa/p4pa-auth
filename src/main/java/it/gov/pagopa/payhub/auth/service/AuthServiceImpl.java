package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{
    private final ExchangeTokenService exchangeTokenService;
    private final UserService userService;

    public AuthServiceImpl(ExchangeTokenService exchangeTokenService, UserService userService) {
        this.exchangeTokenService = exchangeTokenService;
        this.userService = userService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        return exchangeTokenService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        return userService.getUserInfo(accessToken);
    }
}
