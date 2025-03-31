package it.gov.pagopa.payhub.auth.controller;

import io.jsonwebtoken.security.Jwks;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.controller.generated.WellKnownApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WellKnownController implements WellKnownApi {

    private final String jwks;

    public WellKnownController(AccessTokenBuilderService accessTokenBuilderService) {
        this.jwks = "{\"keys\":["+ Jwks.json(accessTokenBuilderService.getJwk())+"]}";
    }

    @Override
    public ResponseEntity<String> jwks() {
        return ResponseEntity.ok(jwks);
    }
}
