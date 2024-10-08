package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.service.exchange.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.utils.JWTValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidateTokenService {
  private final String publicKey;
  private final JWTValidator jwtValidator;

  public ValidateTokenService(JWTValidator jwtValidator,
      @Value("${jwt.access-token.public-key:}") String publicKey) {
    this.jwtValidator = jwtValidator;
    this.publicKey = publicKey;
  }

  public void validate(String token) {
    jwtValidator.validateInternalToken(token, publicKey);
    DecodedJWT jwt = JWT.decode(token);
    validateAccessType(jwt.getHeaderClaim("typ").asString());
  }

  private void validateAccessType(String type) {
    if(!AccessTokenBuilderService.ACCESS_TOKEN_TYPE.equalsIgnoreCase(type)) {
      throw new InvalidTokenException("Invalid token type " + type);
    }
  }
}
