package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static it.gov.pagopa.payhub.model.generated.AuthErrorDTO.CodeEnum.*;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AuthErrorDTO handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request){
        String message = logAndReturnUnauthorizedExceptionMessage(ex, request);

        return new AuthErrorDTO(INVALID_TOKEN, message);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AuthErrorDTO handleTokenExpiredException(TokenExpiredException ex, HttpServletRequest request){
        String message = logAndReturnUnauthorizedExceptionMessage(ex, request);

        return new AuthErrorDTO(TOKEN_EXPIRED_DATE, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AuthErrorDTO handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = ex.getMessage();

        log.info("A MissingServletRequestParameterException occurred handling request {}: HttpStatus 400 - {}",
                AuthExceptionHandler.getRequestDetails(request), message);
        log.debug("Something went wrong handling request", ex);
        return new AuthErrorDTO(INVALID_REQUEST, message);
    }

    private static String logAndReturnUnauthorizedExceptionMessage(RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        log.info("A {} occurred handling request {}: HttpStatus 401 - {}",
                ex.getClass(),
                getRequestDetails(request), message);
        return message;
    }

    private static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
