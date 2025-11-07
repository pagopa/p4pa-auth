package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidScopedAccessTokenRequest;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.controller.generated.AuthnApi;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthnControllerImpl implements AuthnApi {

    private final AuthnService authnService;

    public AuthnControllerImpl(AuthnService authnService) {
        this.authnService = authnService;
    }

    @Override
    public ResponseEntity<AccessToken> postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
        AccessToken accessToken = authnService.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserInfo> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getPrincipal());
    }

    @Override
    public ResponseEntity<Void> logout(String clientId, String token) {
        authnService.logout(clientId, token);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<AccessToken> postLimitedToken(LimitedTokenRequest limitedTokenRequest) {
        if (limitedTokenRequest == null) {
            throw new InvalidScopedAccessTokenRequest("no request body has been provided");
        }
        log.info("POST Limited token request: organizationId={}, app={}, resource={}, resourceId={}", limitedTokenRequest.getOrganizationId(), limitedTokenRequest.getApp(), limitedTokenRequest.getResource(), limitedTokenRequest.getResourceId());
        AccessToken accessToken = authnService.postLimitedToken(limitedTokenRequest);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }
}
