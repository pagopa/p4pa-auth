package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.service.exchange.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidateTokenService {
  private final JWTValidator jwtValidator;

  public ValidateTokenService(JWTValidator jwtValidator) {
    this.jwtValidator = jwtValidator;
  }

  public void validate(String token) {
    jwtValidator.validateInternalToken(token);
    DecodedJWT jwt = JWT.decode(token);
    validateAccessType(jwt.getHeaderClaim("typ").asString());
  }

  private void validateAccessType(String type) {
    if(!AccessTokenBuilderService.ACCESS_TOKEN_TYPE.equalsIgnoreCase(type)) {
      throw new InvalidTokenException("Invalid token type " + type);
    }
  }
}
