package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.service.AuthService;
import it.gov.pagopa.payhub.controller.generated.AuthApi;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthApi {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<AccessToken> postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        authService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
