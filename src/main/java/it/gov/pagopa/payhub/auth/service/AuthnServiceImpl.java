package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.service.m2m.ClientCredentialService;
import it.gov.pagopa.payhub.auth.service.m2m.ValidateClientCredentialsService;
import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthnServiceImpl implements AuthnService {
		private final ClientCredentialService clientCredentialService;
    private final ExchangeTokenService exchangeTokenService;
    private final UserService userService;
    private final LogoutService logoutService;
    private final AuditLoggerService auditService;

    public AuthnServiceImpl(ClientCredentialService clientCredentialService, ExchangeTokenService exchangeTokenService, UserService userService, LogoutService logoutService,
        AuditLoggerService auditService) {
	    this.clientCredentialService = clientCredentialService;
	    this.exchangeTokenService = exchangeTokenService;
      this.userService = userService;
      this.logoutService = logoutService;
      this.auditService = auditService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
      AccessToken accessToken = switch (grantType) {
          case ValidateExternalTokenService.ALLOWED_GRANT_TYPE -> exchangeTokenService.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);
					case ValidateClientCredentialsService.ALLOWED_GRANT_TYPE -> clientCredentialService.postToken(clientId, scope, clientSecret);
					default -> throw new InvalidGrantTypeException("Invalid grantType " + grantType);
				};
      auditService.log(AuditEventType.LOGIN_SUCCESS, Map.of("grantType",grantType), "Authentication success");
      return accessToken;
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
