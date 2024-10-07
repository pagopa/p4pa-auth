package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ValidateTokenServiceTest {
  @Mock
  private ValidateTokenService validateTokenService;

  @Mock
  private JWTValidator jwtValidator;

  @Mock
  private JWT jwtMock;

  private DecodedJWT decodedJWT;

  private static final String PUBLIC_KEY= """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2ovm/rd3g69dq9PisinQ
            6mWy8ZttT8D+GKXCsHZycsGnN7b74TPyYy+4+h+9cgJeizp8RDRrufHjiBrqi/2r
            eOk/rD7ZHbpfQvHK8MYfgIVdtTxYMX/GGdOrX6/5TV2b8e2aCG6GmxF0UuEvxY9o
            TmcZUxnIeDtl/ixz4DQ754eS363qWfEA92opW+jcYzr07sbQtR86e+Z/s/CUeX6W
            1PHNvBqdlAgp2ecr/1DOLq1D9hEANBPSwbt+FM6FNe4vLphi7GTwiB0yaAuy+jE8
            odND6HPvvvmgbK1/2qTHn/HJjWUm11LUC73BszR32BKbdEEhxPQnnwswVekWzPi1
            IwIDAQAB
            -----END PUBLIC KEY-----
            """;

  @Test
  void givenValidRequestThenOk() {
    String validToken = "eyJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI5NzZhYTYzMy0wMTVmLTQ3MDMtYWM3NC03NjE2YjJlN2JkNjQiLCJpYXQiOjE3MjgyOTkwOTksImV4cCI6MTcyODMxMzQ5OX0.l3gHHCdyPxq0AOUO3nFIzDzpp4kgwslS6U3K_KUaQ0VExSsxETGM7N7YiVVu3qXfaNy4H8Q7lvtb8bWThGNehh-SA1sX_U_nmTWhdtt0ULEdQ5sbg5_PH5VGuav-bthzqkeS1zv_TbAGl27HswOOCpdA3LhWzRs4KxA55EnKj0gCjxMHIEYuMxLhc400IKXC8dFk888dv_WZk1FgakdCYUbqOGCK_g7eVxa4N6oaFxJTZHaqviRQOs4YBMszwGhRAl34JBgrR1PYwx3Bsy6wcjEjshilqeOLjGIsUBojFoa8Vfw0oYDJ0OrfiG5EuiyABxqtKkS5b4Hs1qnU63wneg";
    DecodedJWT jwt = JWT.decode(validToken);

    Mockito.doNothing().when(jwtValidator).validateInternalToken(validToken, PUBLIC_KEY);
    Mockito.doNothing().when(validateTokenService).validate(validToken);

    Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(validToken, PUBLIC_KEY));
    Assertions.assertEquals(jwt.getHeaderClaim("typ").asString(), ValidateTokenService.ALLOWED_TYPE);
    Mockito.verify(jwtValidator).validateInternalToken(validToken, PUBLIC_KEY);
  }

  @Test
  void validate_shouldThrowInvalidTokenException_whenTokenTypeIsInvalid() {
    String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI5NzZhYTYzMy0wMTVmLTQ3MDMtYWM3NC03NjE2YjJlN2JkNjQiLCJpYXQiOjE3MjgyOTkwOTksImV4cCI6MTcyODMxMzQ5OX0.NxbnCRBGcr0iftbagyPU-v3140loAQq4k0JaAg1fdTvI3qHBm4CS8za31s7OnRpNQ2ojlww9ApEAowzcjajnVJRo4L5D1W5M0RcVN_wSdBJrNcvPmN7PFKQn37xCbDkQ00I1d4ZLJVbP5hA2FFekJXu_w0NlUhSHsGPQoSYNOJr70fJUQ15K_asr6zi7J5XfbYSMNJBZWdVSCJoVfQDVRaWCq5H4zcBhfCbiOYtYeVDbYygFDWizHTiz9XwF-79aJcjp9VCTduyJ1ROJCBZfnUqZgN4BM75E5H-bmBEEbahqIT3eAY1lYAyv83s3Y5ys-5n6pFWgi6NuvP5vifl78w";
    // When
    Mockito.doNothing().when(jwtValidator).validateInternalToken(invalidToken, PUBLIC_KEY);
    Mockito.when(jwtMock.decodeJwt(invalidToken)).thenReturn(decodedJWT);
    Mockito.doThrow(new InvalidTokenException("InvalidToken")).when(validateTokenService).validate(invalidToken);

    // Then
    Assertions.assertThrows(InvalidTokenException.class, ()->validateTokenService.validate(invalidToken));
  }
}