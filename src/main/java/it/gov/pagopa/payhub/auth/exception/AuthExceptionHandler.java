package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static it.gov.pagopa.payhub.model.generated.AuthErrorDTO.CodeEnum.INVALID_TOKEN;
import static it.gov.pagopa.payhub.model.generated.AuthErrorDTO.CodeEnum.TOKEN_EXPIRED_DATE;

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

    private static String logAndReturnUnauthorizedExceptionMessage(RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        log.info("A {} occurred handling request {}: HttpStatus 401 - {}",
                ex.getClass(),
                getRequestDetails(request), message);
        return message;
    }

    public static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
