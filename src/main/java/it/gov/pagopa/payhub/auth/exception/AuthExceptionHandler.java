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

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AuthErrorDTO handleInvalidTokenException(ServiceException ex, HttpServletRequest request){
        logException(ex, request);
        String message = ex.getMessage();


        return new AuthErrorDTO(ex.getCode(), message);
    }

    public static void logException(ServiceException error, HttpServletRequest request) {
        if(error.isPrintStackTrace()){
            log.info("A {} occurred handling request {} at {}",
                    error.getClass().getSimpleName() ,
                    getRequestDetails(request),
                    error.getStackTrace().length > 0 ? error.getStackTrace()[0] : "UNKNOWN");
        }else {
            log.info("A {} occurred handling request {}: HttpStatus 401 - {}",
                    error.getClass(),
                    getRequestDetails(request), error.getMessage());
        }
    }

    public static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
