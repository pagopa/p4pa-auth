package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.m2m.ClientCredentialService;
import it.gov.pagopa.payhub.auth.service.m2m.ValidateClientCredentialsService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthnServiceImpl implements AuthnService {
    private final ClientCredentialService clientCredentialService;
    private final ExchangeTokenService exchangeTokenService;
    private final UserService userService;
    private final LogoutService logoutService;
    private final AuditLoggerService auditService;
    private final LimitedTokenService limitedTokenService;
    private final OrganizationService organizationService;

    public AuthnServiceImpl(
            ClientCredentialService clientCredentialService,
            ExchangeTokenService exchangeTokenService,
            UserService userService,
            LogoutService logoutService,
            AuditLoggerService auditService,
            LimitedTokenService limitedTokenService,
            OrganizationService organizationService
    ) {
	    this.clientCredentialService = clientCredentialService;
	    this.exchangeTokenService = exchangeTokenService;
        this.userService = userService;
        this.logoutService = logoutService;
        this.auditService = auditService;
        this.limitedTokenService = limitedTokenService;
        this.organizationService=organizationService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
        AccessToken accessToken = switch (grantType) {
            case ValidateExternalTokenService.ALLOWED_GRANT_TYPE -> exchangeTokenService.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);
            case ValidateClientCredentialsService.ALLOWED_GRANT_TYPE -> clientCredentialService.postToken(clientId, scope, clientSecret);
            default -> throw new InvalidGrantTypeException("[INVALID_GRANT_TYPE] Invalid grantType " + grantType);
        };
        Map<String, String> label2value = new HashMap<>();
        label2value.put("grantType", grantType);
        addOrganizationUserInfo(label2value, accessToken);
        auditService.log(AuditEventType.LOGIN_SUCCESS, label2value, "Authentication success");
        return accessToken;
    }

    private void addOrganizationUserInfo(Map<String, String> label2value, AccessToken accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken.getAccessToken());
        Claim organizationIpaCode = jwt.getClaims().get(AccessTokenBuilderService.CLAIM_ORGANIZATION_IPA_CODE);
        if (organizationIpaCode == null) {
            return;
        }
        Organization organization = organizationService.getOrganizationByIpaCode(organizationIpaCode.asString(), accessToken.getAccessToken());
        if(organization==null) {
            return;
        }
        label2value.put("organizationId", String.valueOf(organization.getOrganizationId()));
        label2value.put("organizationName", organization.getOrgName());
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        return userService.getUserInfo(accessToken);
    }

    @Override
    public void logout(String clientId, String token) {
        logoutService.logout(clientId, token);
    }

    @Override
    public AccessToken postLimitedToken(LimitedTokenRequest request) {
        return this.limitedTokenService.generate(request);
    }
}
