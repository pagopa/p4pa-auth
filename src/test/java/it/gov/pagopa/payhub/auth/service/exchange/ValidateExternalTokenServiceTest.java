package it.gov.pagopa.payhub.auth.service.exchange;

import com.github.tomakehurst.wiremock.WireMockServer;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ValidateExternalTokenServiceTest {

    public static final Date EXPIRES_AT = new Date(System.currentTimeMillis() + 3600000);
    private static final String ALLOWED_AUDIENCE = "AUD";
    private static final String ALLOWED_SUBECJECT_ISSUER = "ISS";

    private ValidateExternalTokenService validateExternalTokenService;
    private WireMockServer wireMockServer;
    private JWTValidatorUtils utils;

    @Mock
    private JWTValidator jwtValidator;

    @BeforeEach
    void setup(){
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        utils = new JWTValidatorUtils(wireMockServer);
        validateExternalTokenService = new ValidateExternalTokenService(ALLOWED_AUDIENCE, ALLOWED_SUBECJECT_ISSUER, utils.getUrlJwkProvider(), jwtValidator);
    }

    @AfterEach
    void clean(){
        wireMockServer.stop();
    }

    @Test
    void givenValidRequestThenOk() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE);
        Mockito.verify(jwtValidator, times(1)).validate(subjectToken, wireMockUrl);
    }

    @Test
    void givenInvalidClientThenInvalidExchangeClientException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidExchangeClientException.class, () ->
                validateExternalTokenService.validate("UNEXPECTED_CLIENT_ID", ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE));
    }

    @Test
    void givenInvalidGrantTypeException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidGrantTypeException.class, () ->
                validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, "UNEXPECTED_GRANT_TYPE", subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE));
    }

    @Test
    void givenInvalidSubjectTokenIssuerThenInvalidTokenIssuerException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidTokenIssuerException.class, () ->
                validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, "UNEXPECTED_SUBECJECT_ISSUER", ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE));
    }

    @Test
    void givenInvalidScopeThenInvalidExchangeRequestException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidExchangeRequestException.class, () ->
                validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, "UNEXPECTED_SCOPE"));
    }

    @Test
    void givenInvalidIssuerClaimThenInvalidTokenException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims("ISS_FAKE", ALLOWED_AUDIENCE);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidTokenException.class, () ->
                validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE));

    }

    @Test
    void givenInvalidAudienceClaimThenInvalidTokenException() throws Exception {
        String subjectToken = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ALLOWED_SUBECJECT_ISSUER, "AUD_FAKE");

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(subjectToken, wireMockUrl)).thenReturn(claimsMap);

        assertThrows(InvalidTokenException.class, () ->
                validateExternalTokenService.validate(ValidateExternalTokenService.ALLOWED_CLIENT_ID, ValidateExternalTokenService.ALLOWED_GRANT_TYPE, subjectToken, ALLOWED_SUBECJECT_ISSUER, ValidateExternalTokenService.ALLOWED_SUBJECT_TOKEN_TYPE, ValidateExternalTokenService.ALLOWED_SCOPE));

    }

    private Map<String, String> createJWKClaims (String iss, String aud){
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", iss);
        claims.put("aud", aud);
        claims.put("exp", "1715267318");
        claims.put("jti", "my-key-id");
        return claims;
    }
}
