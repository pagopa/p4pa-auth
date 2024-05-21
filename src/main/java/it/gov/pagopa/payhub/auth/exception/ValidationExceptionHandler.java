package it.gov.pagopa.payhub.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.openapi.example.model.AuthErrorDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static org.openapi.example.model.AuthErrorDTO.CodeEnum.INVALID_REQUEST;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    private final AuthErrorDTO templateValidationErrorDTO;

    public ValidationExceptionHandler(@Nullable AuthErrorDTO templateValidationErrorDTO) {
        this.templateValidationErrorDTO = Optional.ofNullable(templateValidationErrorDTO)
                .orElse(new AuthErrorDTO(INVALID_REQUEST, "Invalid request"));
    }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public AuthErrorDTO handleMissingServletRequestParameterException(
          MissingServletRequestParameterException ex, HttpServletRequest request) {

    String message = ex.getMessage();

    log.info("A MissingServletRequestParameterException occurred handling request {}: HttpStatus 400 - {}",
        AuthExceptionHandler.getRequestDetails(request), message);
    log.debug("Something went wrong handling request", ex);
    return new AuthErrorDTO(templateValidationErrorDTO.getCode(), message);
  }
}
