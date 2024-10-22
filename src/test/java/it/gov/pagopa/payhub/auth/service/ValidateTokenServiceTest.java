package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ValidateTokenServiceTest {

  private ValidateTokenService validateTokenService;

  @Mock
  private JWTValidator jwtValidator;

  @Mock
  private JWT jwtMock;

  private DecodedJWT decodedJWT;

  @BeforeEach
  void setup(){
    validateTokenService = new ValidateTokenService(jwtValidator);
  }

  @Test
  void givenValidJWTThenOk() {
    String validToken = "eyJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI5NzZhYTYzMy0wMTVmLTQ3MDMtYWM3NC03NjE2YjJlN2JkNjQiLCJpYXQiOjE3MjgyOTkwOTksImV4cCI6MTcyODMxMzQ5OX0.l3gHHCdyPxq0AOUO3nFIzDzpp4kgwslS6U3K_KUaQ0VExSsxETGM7N7YiVVu3qXfaNy4H8Q7lvtb8bWThGNehh-SA1sX_U_nmTWhdtt0ULEdQ5sbg5_PH5VGuav-bthzqkeS1zv_TbAGl27HswOOCpdA3LhWzRs4KxA55EnKj0gCjxMHIEYuMxLhc400IKXC8dFk888dv_WZk1FgakdCYUbqOGCK_g7eVxa4N6oaFxJTZHaqviRQOs4YBMszwGhRAl34JBgrR1PYwx3Bsy6wcjEjshilqeOLjGIsUBojFoa8Vfw0oYDJ0OrfiG5EuiyABxqtKkS5b4Hs1qnU63wneg";
    DecodedJWT jwt = JWT.decode(validToken);

    Mockito.doNothing().when(jwtValidator).validateInternalToken(validToken);

    validateTokenService.validate(validToken);

    Assertions.assertDoesNotThrow(() -> jwtValidator.validateInternalToken(validToken));
    Assertions.assertDoesNotThrow(() -> validateTokenService.validate(validToken));
    Assertions.assertEquals(AccessTokenBuilderService.ACCESS_TOKEN_TYPE, jwt.getHeaderClaim("typ").asString());
  }

  @Test
  void givenInvalidJWTTypeThenInvalidTokenException() {
    String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI5NzZhYTYzMy0wMTVmLTQ3MDMtYWM3NC03NjE2YjJlN2JkNjQiLCJpYXQiOjE3MjgyOTkwOTksImV4cCI6MTcyODMxMzQ5OX0.NxbnCRBGcr0iftbagyPU-v3140loAQq4k0JaAg1fdTvI3qHBm4CS8za31s7OnRpNQ2ojlww9ApEAowzcjajnVJRo4L5D1W5M0RcVN_wSdBJrNcvPmN7PFKQn37xCbDkQ00I1d4ZLJVbP5hA2FFekJXu_w0NlUhSHsGPQoSYNOJr70fJUQ15K_asr6zi7J5XfbYSMNJBZWdVSCJoVfQDVRaWCq5H4zcBhfCbiOYtYeVDbYygFDWizHTiz9XwF-79aJcjp9VCTduyJ1ROJCBZfnUqZgN4BM75E5H-bmBEEbahqIT3eAY1lYAyv83s3Y5ys-5n6pFWgi6NuvP5vifl78w";
    // When
    Mockito.doNothing().when(jwtValidator).validateInternalToken(invalidToken);
    Mockito.when(jwtMock.decodeJwt(invalidToken)).thenReturn(decodedJWT);

    // Then
    Assertions.assertThrows(InvalidTokenException.class, ()->validateTokenService.validate(invalidToken));
  }
}