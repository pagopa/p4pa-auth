package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthnServiceImpl implements AuthnService {
    private final ExchangeTokenService exchangeTokenService;
    private final UserService userService;
    private final LogoutService logoutService;

    public AuthnServiceImpl(ExchangeTokenService exchangeTokenService, UserService userService, LogoutService logoutService) {
        this.exchangeTokenService = exchangeTokenService;
        this.userService = userService;
        this.logoutService = logoutService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        return exchangeTokenService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        return userService.getUserInfo(accessToken);
    }

    @Override
    public void logout(String clientId, String token) {
        logoutService.logout(clientId, token);
    }
}
