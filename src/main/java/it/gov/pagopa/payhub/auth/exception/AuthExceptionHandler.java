package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.exception.common.CommonExceptionHandler;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.dto.generated.AuthErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler extends CommonExceptionHandler {

    private final AuditLoggerService auditService;

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

    @ExceptionHandler(InvalidOrganizationException.class)
    public ResponseEntity<AuthErrorDTO> handleInvalidOrganizationException(InvalidOrganizationException ex, HttpServletRequest request) {
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


    @ExceptionHandler({InvalidScopedAccessTokenRequest.class})
    public ResponseEntity<AuthErrorDTO> handleInvalidScopedAccessTokenRequest(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    private void logAuditFailure(RuntimeException ex, HttpServletRequest request, String baseDescription) {
        String errorType = ex.getClass().getSimpleName();
        auditService.log(
            AuditEventType.LOGIN_FAILURE,
            Map.of("error_type", errorType, "request_uri", request.getRequestURI()),
            baseDescription + " Exception: " + ex.getMessage()
        );
    }

}
