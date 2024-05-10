package it.gov.pagopa.payhub.auth.configuration;

import it.gov.pagopa.payhub.auth.exception.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.TokenExpiredException;
import it.gov.pagopa.payhub.common.web.exception.ServiceException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceExceptionConfig {

  @Bean
  public Map<Class<? extends ServiceException>, HttpStatus> serviceExceptionMapper() {
    Map<Class<? extends ServiceException>, HttpStatus> exceptionMap = new HashMap<>();

    //Unauthorized
    exceptionMap.put(TokenExpiredException.class, HttpStatus.UNAUTHORIZED);
    exceptionMap.put(InvalidTokenException.class, HttpStatus.UNAUTHORIZED);

    return exceptionMap;
  }

}
