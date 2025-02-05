package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(value = {AuthExceptionHandlerTest.TestController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        AuthExceptionHandlerTest.TestController.class,
        AuthExceptionHandler.class})
class AuthExceptionHandlerTest {

    public static final String DATA = "data";
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private TestController testControllerSpy;
    @MockitoSpyBean
    private RequestMappingHandlerAdapter requestMappingHandlerAdapterSpy;

    @RestController
    @Slf4j
    static class TestController {

        @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
        String testEndpoint(@RequestParam(DATA) String data) {
            return "OK";
        }
    }

    @Test
    void handleInvalidTokenException() throws Exception {
        doThrow(new InvalidTokenException("Error")).when(testControllerSpy).testEndpoint(DATA);

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

    }

    @Test
    void handleTokenExpiredException() throws Exception {
        doThrow(new TokenExpiredException("Error")).when(testControllerSpy).testEndpoint(DATA);

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_grant"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

    }

    @Test
    void handleMissingServletRequestParameterException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Required request parameter 'data' for method parameter type String is not present"));

    }

    @Test
    void handleRuntimeExceptionError() throws Exception {
        doThrow(new RuntimeException("Error")).when(testControllerSpy).testEndpoint(DATA);

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("AUTH_GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));
    }

    @Test
    void handleGenericServletException() throws Exception {
        doThrow(new ServletException("Error"))
                .when(requestMappingHandlerAdapterSpy).handle(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("AUTH_GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));
    }

    @Test
    void handle4xxHttpServletException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .accept("application/hal+json"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("No acceptable representation"));
    }

    @Test
    void handle5xxHttpServletException() throws Exception {
        doThrow(new ServerErrorException("Error", new RuntimeException("Error")))
                .when(requestMappingHandlerAdapterSpy).handle(any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("AUTH_GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("500 INTERNAL_SERVER_ERROR \"Error\""));
    }

    @Test
    void handleViolationException() throws Exception {
        doThrow(new ConstraintViolationException("Error", Set.of())).when(testControllerSpy).testEndpoint(DATA);

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));
    }
}