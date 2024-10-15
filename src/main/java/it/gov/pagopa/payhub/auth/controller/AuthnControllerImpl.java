package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.controller.generated.AuthnApi;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthnControllerImpl implements AuthnApi {

    private final AuthnService authnService;

    public AuthnControllerImpl(AuthnService authnService) {
        this.authnService = authnService;
    }

    @Override
    public ResponseEntity<AccessToken> postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType) {
        AccessToken accessToken = authnService.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType);
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
}
