package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.service.a2a.ClientCredentialService;
import it.gov.pagopa.payhub.auth.service.a2a.ValidateClientCredentialsService;
import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthnServiceImpl implements AuthnService {
		private final ClientCredentialService clientCredentialService;
    private final ExchangeTokenService exchangeTokenService;
    private final UserService userService;
    private final LogoutService logoutService;

    public AuthnServiceImpl(ClientCredentialService clientCredentialService, ExchangeTokenService exchangeTokenService, UserService userService, LogoutService logoutService) {
	    this.clientCredentialService = clientCredentialService;
	    this.exchangeTokenService = exchangeTokenService;
      this.userService = userService;
      this.logoutService = logoutService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
				return switch (grantType) {
          case ValidateExternalTokenService.ALLOWED_GRANT_TYPE -> exchangeTokenService.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);
					case ValidateClientCredentialsService.ALLOWED_GRANT_TYPE -> clientCredentialService.postToken(clientId, scope, clientSecret);
					default -> throw new InvalidGrantTypeException("Invalid grantType " + grantType);
				};
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
