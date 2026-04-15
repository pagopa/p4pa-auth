package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.auth.utils.Utilities;
import it.gov.pagopa.payhub.dto.generated.AuthErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.slf4j.event.Level;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    private final AuditLoggerService auditService;

    private static final String ERROR_MESSAGE_FORMAT = "[%s] %s";

    public AuthExceptionHandler(AuditLoggerService auditService) {
        this.auditService = auditService;
    }

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidGrantError(RuntimeException ex, HttpServletRequest request) {
        logAuditFailure(ex, request, "Unauthorized access or invalid token/grant type.");
        return handleException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_GRANT);
    }

    @ExceptionHandler({InvalidExchangeClientException.class, ClientUnauthorizedException.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidClientError(RuntimeException ex, HttpServletRequest request) {
        logAuditFailure(ex, request, "Client unauthorized or invalid client configuration.");
        return handleException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_CLIENT);
    }

    @ExceptionHandler({InvalidExchangeRequestException.class, InvalidTokenIssuerException.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidRequestError(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler({InvalidGrantTypeException.class})
    public ResponseEntity<AuthErrorDTO> handleUnsupportedGrantType(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.UNSUPPORTED_GRANT_TYPE);
    }

    @ExceptionHandler({UserUnauthorizedException.class})
    public ResponseEntity<AuthErrorDTO> handleUserUnauthorizedException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.FORBIDDEN, AuthErrorDTO.ErrorEnum.AUTH_USER_UNAUTHORIZED);
    }

    @ExceptionHandler({OperatorNotFoundException.class, ClientNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleOperatorNotFoundException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        logException(ex, request, httpStatus);
        return ResponseEntity.status(httpStatus).body(null);
    }

    @ExceptionHandler(M2MClientConflictException.class)
    public ResponseEntity<String> handleConflictException(RuntimeException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        logException(ex, request, httpStatus);
        return ResponseEntity.status(httpStatus).body(null);
    }

    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<AuthErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler({ServletException.class, ErrorResponseException.class})
    public ResponseEntity<AuthErrorDTO> handleServletException(Exception ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        AuthErrorDTO.ErrorEnum errorCode = AuthErrorDTO.ErrorEnum.AUTH_GENERIC_ERROR;
        if (ex instanceof ErrorResponse errorResponse) {
            httpStatus = HttpStatus.valueOf((errorResponse.getStatusCode().value()));
            if (httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND)) {
                errorCode = AuthErrorDTO.ErrorEnum.AUTH_NOT_FOUND;
            } else if (httpStatus.is4xxClientError()) {
                errorCode = AuthErrorDTO.ErrorEnum.INVALID_REQUEST;
            }
        }
        return handleException(ex, request, httpStatus, errorCode);
    }

    @ExceptionHandler({InvalidScopedAccessTokenRequest.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidScopedAccessTokenRequest(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<AuthErrorDTO> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorDTO.ErrorEnum.AUTH_GENERIC_ERROR);
    }

    static ResponseEntity<AuthErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatus httpStatus, AuthErrorDTO.ErrorEnum errorEnum) {
        logException(ex, request, httpStatus);

        Pair<String, String> code2message = Optional.of(request.getRequestURI())
                .filter(path -> path.contains("/crud/"))
                .map(path -> buildCrudErrorMessage(path, httpStatus, ex))
                .orElseGet(() -> buildReturnedMessage(ex));

        String code = Objects.requireNonNullElse(code2message.getLeft(), errorEnum.getValue());
        String message = code2message.getRight();

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AuthErrorDTO(errorEnum, code, String.format(ERROR_MESSAGE_FORMAT, code, message), Utilities.getTraceId()));
    }

    private static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus) {
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

    private static Pair<String, String> buildReturnedMessage(Exception ex) {
        switch (ex) {
            case HttpMessageNotReadableException httpMessageNotReadableException -> {
                String errorMsg = "Required request body is missing";
                if (httpMessageNotReadableException.getCause() instanceof DatabindException jsonMappingException) {
                    errorMsg = "Cannot parse body. " +
                            jsonMappingException.getPath().stream()
                                    .map(JacksonException.Reference::getPropertyName)
                                    .collect(Collectors.joining(".")) +
                            ": " + jsonMappingException.getOriginalMessage();
                } else if (httpMessageNotReadableException.getCause() instanceof JacksonException jacksonException) {
                    errorMsg = "Cannot parse body. " + jacksonException.getOriginalMessage();
                }
                return Pair.of(AuthErrorDTO.ErrorEnum.INVALID_REQUEST.name(), errorMsg);
            }
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                return Pair.of(AuthErrorDTO.ErrorEnum.INVALID_REQUEST.name(),
                        "Invalid request content." +
                        methodArgumentNotValidException.getBindingResult()
                                .getAllErrors().stream()
                                .map(e -> " " +
                                        (e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName()) +
                                        ": " + e.getDefaultMessage())
                                .sorted()
                                .collect(Collectors.joining(";")));
            }
            case ConstraintViolationException constraintViolationException -> {
                return Pair.of(AuthErrorDTO.ErrorEnum.INVALID_REQUEST.name(),
                        "Invalid request content." +
                        constraintViolationException.getConstraintViolations()
                                .stream()
                                .map(e -> " " + e.getPropertyPath() + ": " + e.getMessage())
                                .sorted()
                                .collect(Collectors.joining(";")));
            }
            case BaseBusinessException businessException -> {
                return Pair.of(businessException.getCode(), businessException.getMessage());
            }
            default -> {
                if (ex.getCause() instanceof HttpHostConnectException) {
                    return Pair.of("AUTH_CONNECTION_ERROR", ex.getMessage());
                }
                return Pair.of(null, ex.getMessage());
            }
        }
    }

    private static Pair<String, String> buildCrudErrorMessage(String requestPath, HttpStatus httpStatus, Exception ex) {
        String entity = requestPath.split("/crud/")[1].split("/")[0].replaceAll("s$", "");
        String entityCode = entity.replace("-", "_").toUpperCase();
        return Pair.of(entityCode + "_" + httpStatus.name(), buildReturnedMessage(ex).getValue());
    }

    private void logAuditFailure(RuntimeException ex, HttpServletRequest request, String baseDescription) {
        String errorType = ex.getClass().getSimpleName();
        auditService.log(
            AuditEventType.LOGIN_FAILURE,
            Map.of("error_type", errorType, "request_uri", request.getRequestURI()),
            baseDescription + " Exception: " + ex.getMessage()
        );
    }

    static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
