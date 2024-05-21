package it.gov.pagopa.payhub.auth.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import it.gov.pagopa.payhub.auth.utils.JWTValidatorUtils;
import openapi.pagopa.payhub.model.AuthErrorDTO;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    public static final Date EXPIRES_AT = new Date(System.currentTimeMillis() + 3600000);
    private static final String AUD = "AUD";
    private static final String ISS = "ISS";

    private AuthService authService;
    private WireMockServer wireMockServer;
    private JWTValidatorUtils utils;

    @Mock
    private JWTValidator jwtValidator;

    @BeforeEach
    void setup(){
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        utils = new JWTValidatorUtils(wireMockServer);
        authService = new AuthServiceImpl(AUD, ISS, utils.getUrlJwkProvider(), jwtValidator);
    }

    @AfterEach
    void clean(){
        wireMockServer.stop();
    }
    @Test
    void authTokenOk() throws Exception {
        String token = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ISS, AUD);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        authService.authToken(token);
        Mockito.verify(jwtValidator, times(1)).validate(token, wireMockUrl);
    }

    @Test
    void authTokenWrongIss() throws Exception {
        String token = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims("ISS_FAKE", AUD);

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        InvalidTokenException result =
                assertThrows(InvalidTokenException.class, () ->
                                authService.authToken(token));

        assertEquals(AuthErrorDTO.CodeEnum.INVALID_TOKEN.getValue(), result.getCode());
    }

    @Test
    void authTokenWrongAud() throws Exception {
        String token = utils.generateJWK(EXPIRES_AT);
        Map<String, String> claimsMap = createJWKClaims(ISS, "AUD_FAKE");

        String wireMockUrl = utils.getUrlJwkProvider();
        when(jwtValidator.validate(token, wireMockUrl)).thenReturn(claimsMap);

        InvalidTokenException result =
                assertThrows(InvalidTokenException.class, () ->
                        authService.authToken(token));

        assertEquals(AuthErrorDTO.CodeEnum.INVALID_TOKEN.getValue(), result.getCode());
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
