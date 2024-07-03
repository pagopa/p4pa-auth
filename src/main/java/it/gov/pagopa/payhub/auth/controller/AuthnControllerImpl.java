package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.controller.generated.AuthnApi;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthnControllerImpl implements AuthnApi {

    private final AuthnService authnService;

    public AuthnControllerImpl(AuthnService authnService) {
        this.authnService = authnService;
    }

    @Override
    public ResponseEntity<AccessToken> postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        AccessToken accessToken = authnService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserInfo> getUserInfo() {
        return ResponseEntity.ok((UserInfo)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Override
    public ResponseEntity<Void> logout(String clientId, String token) {
        authnService.logout(clientId, token);
        return ResponseEntity.ok(null);
    }
}
