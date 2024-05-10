package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.service.AuthService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void authToken(String token) {
        authService.authToken(token);
    }
}
