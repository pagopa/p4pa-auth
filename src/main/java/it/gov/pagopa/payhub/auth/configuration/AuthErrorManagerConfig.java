package it.gov.pagopa.payhub.auth.configuration;

import it.gov.pagopa.payhub.auth.constants.AuthConstants;
import it.gov.pagopa.payhub.auth.exception.dto.ErrorDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthErrorManagerConfig {
  @Bean
  ErrorDTO defaultErrorDTO() {
    return new ErrorDTO(
            AuthConstants.ExceptionCode.GENERIC_ERROR,
            "A generic error occurred"
    );
  }
  @Bean
  ErrorDTO templateValidationErrorDTO(){
    return new ErrorDTO(AuthConstants.ExceptionCode.INVALID_REQUEST, null);
  }
}
