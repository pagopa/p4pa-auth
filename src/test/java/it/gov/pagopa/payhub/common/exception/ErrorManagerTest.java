package it.gov.pagopa.payhub.common.exception;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.payhub.auth.configuration.ServiceExceptionConfig;
import it.gov.pagopa.payhub.auth.exception.InvalidTokenException;
import it.gov.pagopa.payhub.common.utils.MemoryAppender;
import it.gov.pagopa.payhub.common.web.exception.ErrorManager;
import it.gov.pagopa.payhub.common.web.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {ErrorManagerTest.TestController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {ErrorManagerTest.TestController.class, ErrorManager.class, ServiceExceptionConfig.class})
class ErrorManagerTest {

  public static final String EXPECTED_DEFAULT_ERROR = "{\"code\":\"Error\",\"message\":\"Something gone wrong\"}";
  @Autowired
  private MockMvc mockMvc;

  private static MemoryAppender memoryAppender;

  @SpyBean
  private TestController testControllerSpy;

  @RestController
  @Slf4j
  static class TestController {

    @GetMapping("/test")
    String testEndpoint() {
      return "OK";
    }
  }

  @BeforeAll
  static void configureMemoryAppender(){
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    memoryAppender.start();
  }

  @BeforeEach
  void clearAndAppendMemoryAppender(){
    memoryAppender.reset();

    ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ErrorManager.class.getName());
    logger.setLevel(ch.qos.logback.classic.Level.INFO);
    logger.addAppender(memoryAppender);
  }

  @Test
  void handleExceptionInvalidToken() throws Exception {
    Mockito.doThrow(new InvalidTokenException("401", "InvalidTokenError"))
            .when(testControllerSpy).testEndpoint();

    String errorInvalidTokenException = "{\"code\":\"401\",\"message\":\"InvalidTokenError\"}";

    mockMvc.perform(MockMvcRequestBuilders.get("/test")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.content().json(errorInvalidTokenException));
  }
  @Test
  void handleExceptionInvalidTokenWithStackTrace() throws Exception {
    Mockito.doThrow(new InvalidTokenException("401", "InvalidTokenError", true, new Throwable()))
        .when(testControllerSpy).testEndpoint();

    String errorInvalidTokenExceptionStatusAndTitleAndMessageAndThrowable= "{\"code\":\"401\",\"message\":\"InvalidTokenError\"}";

    mockMvc.perform(MockMvcRequestBuilders.get("/test")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.content().json(errorInvalidTokenExceptionStatusAndTitleAndMessageAndThrowable));

    checkLogException(memoryAppender, "Something went wrong handling request GET /test");
    memoryAppender.reset();

  }

  @Test
  void handleServiceExceptionWithoutHTTPStatus() throws Exception {

    Mockito.doThrow(ServiceException.class)
            .when(testControllerSpy).testEndpoint();

    mockMvc.perform(MockMvcRequestBuilders.get("/test")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.content().json(EXPECTED_DEFAULT_ERROR));

    checkLogException(memoryAppender, "A ServiceException occurred handling request GET /test at UNKNOWN");
    memoryAppender.reset();
  }

  @Test
  void handleExceptionRuntimeException() throws Exception {
    Mockito.doThrow(RuntimeException.class)
        .when(testControllerSpy).testEndpoint();

    mockMvc.perform(MockMvcRequestBuilders.get("/test")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.content().json(EXPECTED_DEFAULT_ERROR));
  }

  public static void checkLogException(MemoryAppender memoryAppender, String expectedLoggedMessage) {
    String loggedMessage = memoryAppender.getLoggedEvents().get(0).getFormattedMessage();
    Assertions.assertTrue(Pattern.matches(expectedLoggedMessage, loggedMessage),
            "Unexpected logged message: " + loggedMessage);
  }

}
