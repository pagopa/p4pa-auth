package it.gov.pagopa.payhub.auth.exception;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @SpyBean
    private TestController testControllerSpy;

    @RestController
    @Slf4j
    static class TestController {

        @GetMapping("/test")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("AUTH_INVALID_TOKEN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"));

    }

    @Test
    void handleTokenExpiredException() throws Exception {
        doThrow(new TokenExpiredException("Error")).when(testControllerSpy).testEndpoint(DATA);

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("AUTH_TOKEN_EXPIRED_DATE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"));

    }

    @Test
    void handleMissingServletRequestParameterException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("AUTH_INVALID_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Required request parameter 'data' for method parameter type String is not present"));

    }
}