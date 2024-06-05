package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.service.AuthService;
import it.gov.pagopa.payhub.controller.generated.AuthApi;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
public class AuthControllerImpl implements AuthApi {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<AccessToken> postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        AccessToken accessToken = authService.postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserInfo> getUserInfo() {
        String authorization = getAuthorizationHeader();
        if(StringUtils.hasText(authorization)){
            return ResponseEntity.ok(authService.getUserInfo(authorization.replace("Bearer ", "")));
        } else {
            throw new InvalidAccessTokenException("Missing authorization header");
        }
    }

    private static String getAuthorizationHeader() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }
}
