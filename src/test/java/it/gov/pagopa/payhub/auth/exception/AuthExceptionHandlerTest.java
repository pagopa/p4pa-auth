package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.common.CommonExceptionHandlerTest;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;

class AuthExceptionHandlerTest extends CommonExceptionHandlerTest {

    @MockitoBean
    private AuditLoggerService auditLoggerService;

    @Test
    void handleInvalidTokenException() throws Exception {
        doThrow(new InvalidTokenException("ERRORCODE", "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("ERRORCODE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));

    }

    @Test
    void handleTokenExpiredException() throws Exception {
        doThrow(new TokenExpiredException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("TOKEN_EXPIRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));

    }

    @Test
    void handleInvalidOrganizationException() throws Exception {
        doThrow(new InvalidOrganizationException("INVALID_ORGANIZATION", "Error"))
                .when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_ORGANIZATION"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

}