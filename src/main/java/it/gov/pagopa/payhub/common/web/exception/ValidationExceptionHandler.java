package it.gov.pagopa.payhub.common.web.exception;

import it.gov.pagopa.payhub.common.web.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    private final ErrorDTO templateValidationErrorDTO;

    public ValidationExceptionHandler(@Nullable ErrorDTO templateValidationErrorDTO) {
        this.templateValidationErrorDTO = Optional.ofNullable(templateValidationErrorDTO)
                .orElse(new ErrorDTO("INVALID_REQUEST", "Invalid request"));
    }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleMissingServletRequestParameterException(
          MissingServletRequestParameterException ex, HttpServletRequest request) {

    String message = ex.getMessage();

    log.info("A MissingServletRequestParameterException occurred handling request {}: HttpStatus 400 - {}",
        ErrorManager.getRequestDetails(request), message);
    log.debug("Something went wrong handling request", ex);
    return new ErrorDTO(templateValidationErrorDTO.getCode(), message);
  }
}
