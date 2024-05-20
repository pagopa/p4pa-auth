package it.gov.pagopa.payhub.auth.exception.dto;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.openapi.example.model.AuthErrorDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static org.openapi.example.model.AuthErrorDTO.CodeEnum.GENERIC_ERROR;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    private final AuthErrorDTO templateValidationErrorDTO;

    public AuthExceptionHandler(@Nullable AuthErrorDTO templateValidationErrorDTO) {
        this.templateValidationErrorDTO = Optional.ofNullable(templateValidationErrorDTO)
                .orElse(new AuthErrorDTO(GENERIC_ERROR, "Invalid request"));
    }

    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AuthErrorDTO handleInvalidTokenException(Exception ex, HttpServletRequest request){
        String message = ex.getMessage();

        log.info("A {} occurred handling request {}: HttpStatus 401 - {}",
                ex.getClass(),
                getRequestDetails(request), message);
        return new AuthErrorDTO(templateValidationErrorDTO.getCode(), message);
    }

    public static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }

}
