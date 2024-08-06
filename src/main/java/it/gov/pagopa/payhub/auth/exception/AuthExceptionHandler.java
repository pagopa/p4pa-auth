package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidGrantError(RuntimeException ex, HttpServletRequest request){
        return handleAuthErrorException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_GRANT);
    }

    @ExceptionHandler(InvalidExchangeClientException.class)
    public ResponseEntity<AuthErrorDTO> handleInvalidClientError(RuntimeException ex, HttpServletRequest request){
        return handleAuthErrorException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_CLIENT);
    }

    @ExceptionHandler({InvalidExchangeRequestException.class, InvalidTokenIssuerException.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidRequestError(RuntimeException ex, HttpServletRequest request){
        return handleAuthErrorException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler({InvalidGrantTypeException.class})
    public ResponseEntity<AuthErrorDTO> handleUnsupportedGrantType(RuntimeException ex, HttpServletRequest request){
        return handleAuthErrorException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.UNSUPPORTED_GRANT_TYPE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AuthErrorDTO handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = ex.getMessage();

        log.info("A MissingServletRequestParameterException occurred handling request {}: HttpStatus 400 - {}",
                AuthExceptionHandler.getRequestDetails(request), message);
        log.debug("Something went wrong handling request", ex);
        return new AuthErrorDTO(AuthErrorDTO.ErrorEnum.INVALID_REQUEST, message);
    }

    @ExceptionHandler({UserUnauthorizedException.class})
    public ResponseEntity<AuthErrorDTO> handleUserUnauthorizedException(RuntimeException ex, HttpServletRequest request){
        return handleAuthErrorException(ex, request, HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.AUTH_USER_UNAUTHORIZED);
    }

    @ExceptionHandler(OperatorNotFoundException.class)
    public ResponseEntity<String> handleOperatorNotFoundException(OperatorNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    static ResponseEntity<AuthErrorDTO> handleAuthErrorException(RuntimeException ex, HttpServletRequest request, HttpStatus httpStatus, AuthErrorDTO.ErrorEnum errorEnum) {
        String message = logException(ex, request, httpStatus);

        return ResponseEntity
                .status(httpStatus)
                .body(new AuthErrorDTO(errorEnum, message));
    }

    private static String logException(RuntimeException ex, HttpServletRequest request, HttpStatus httpStatus) {
        String message = ex.getMessage();
        log.info("A {} occurred handling request {}: HttpStatus {} - {}",
                ex.getClass(),
                getRequestDetails(request),
                httpStatus.value(),
                message);
        return message;
    }

    static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
