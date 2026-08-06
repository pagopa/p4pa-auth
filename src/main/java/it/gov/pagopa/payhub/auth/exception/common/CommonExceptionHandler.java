package it.gov.pagopa.payhub.auth.exception.common;

import it.gov.pagopa.payhub.dto.generated.ErrorFieldDTO;
import it.gov.pagopa.payhub.dto.generated.AuthErrorDTO;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoderService;
import it.gov.pagopa.payhub.auth.utils.Utilities;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

@Slf4j
public class CommonExceptionHandler {

  private static final ExceptionMessageTranscoderService exceptionMessageTranscoderService = new ExceptionMessageTranscoderService();

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<AuthErrorDTO> handleConflictException(ConflictException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.CONFLICT, AuthErrorDTO.ErrorEnum.AUTH_CONFLICT);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<AuthErrorDTO> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.FORBIDDEN, AuthErrorDTO.ErrorEnum.AUTH_FORBIDDEN);
  }

  @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ConversionFailedException.class, InvalidValueException.class})
  public ResponseEntity<AuthErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity<AuthErrorDTO> handleNotAuthorizedException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.AUTH_USER_UNAUTHORIZED);
  }

  @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
  public ResponseEntity<AuthErrorDTO> handleInvokedHttpClientTooManyRequestsError(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.TOO_MANY_REQUESTS, AuthErrorDTO.ErrorEnum.AUTH_TOO_MANY_REQUESTS);
  }

  @ExceptionHandler({ServletException.class, ErrorResponseException.class})
  public ResponseEntity<AuthErrorDTO> handleServletException(Exception ex, HttpServletRequest request) {
    HttpStatusCode httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    AuthErrorDTO.ErrorEnum errorCode = AuthErrorDTO.ErrorEnum.AUTH_GENERIC_ERROR;
    if (ex instanceof ErrorResponse errorResponse) {
      httpStatus = errorResponse.getStatusCode();
      if (httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
        errorCode = AuthErrorDTO.ErrorEnum.AUTH_NOT_FOUND;
      } else if (httpStatus.is4xxClientError()) {
        errorCode = AuthErrorDTO.ErrorEnum.INVALID_REQUEST;
      }
    }
    return handleException(ex, request, httpStatus, errorCode);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<AuthErrorDTO> handleResourceNotFoundException(NotFoundException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.NOT_FOUND, AuthErrorDTO.ErrorEnum.AUTH_NOT_FOUND);
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<AuthErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorDTO.ErrorEnum.AUTH_GENERIC_ERROR);
  }

  @ExceptionHandler({AuthorizationDeniedException.class})
  public ResponseEntity<AuthErrorDTO> handleAuthorizationDeniedException(Exception ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.FORBIDDEN, AuthErrorDTO.ErrorEnum.AUTH_USER_UNAUTHORIZED);
  }

  public static ResponseEntity<AuthErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, AuthErrorDTO.ErrorEnum errorEnum) {
    logException(ex, request, httpStatus);

    ExceptionMessageTranscoded code2message = buildReturnedMessage(ex);

    String code = Objects.requireNonNullElse(code2message.getCode(), errorEnum.getValue());
    String message = code2message.getMessage();
    List<ErrorFieldDTO> fields = code2message.getFields();

    return ResponseEntity
      .status(httpStatus)
      .contentType(MediaType.APPLICATION_JSON)
      .body(new AuthErrorDTO(errorEnum, code, message, fields, Utilities.getTraceId()));
  }

  public static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus) {
    boolean printStackTrace = httpStatus.is5xxServerError();
    Level logLevel = printStackTrace ? Level.ERROR : Level.INFO;
    log.makeLoggingEventBuilder(logLevel)
      .log("A {} occurred handling request {}: HttpStatus {} - {}",
        ex.getClass(),
        getRequestDetails(request),
        httpStatus.value(),
        ex.getMessage(),
        printStackTrace ? ex : null
      );
    if (!printStackTrace && log.isDebugEnabled() && ex.getCause() != null) {
      log.debug("CausedBy: ", ex.getCause());
    }
  }

  private static ExceptionMessageTranscoded buildReturnedMessage(Exception ex) {
    return exceptionMessageTranscoderService.transcode(ex);
  }

  public static String getRequestDetails(HttpServletRequest request) {
    String method = Objects.requireNonNullElse(request.getMethod(), "")
      .replace('\n', '_')
      .replace('\r', '_');
    String requestUri = Objects.requireNonNullElse(request.getRequestURI(), "")
      .replace('\n', '_')
      .replace('\r', '_');
    return "%s %s".formatted(method, requestUri);
  }
}
