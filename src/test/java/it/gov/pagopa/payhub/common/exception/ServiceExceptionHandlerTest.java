package it.gov.pagopa.payhub.common.exception;


import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.payhub.common.utils.MemoryAppender;
import it.gov.pagopa.payhub.common.web.exception.ErrorManager;
import it.gov.pagopa.payhub.common.web.exception.ServiceException;
import it.gov.pagopa.payhub.common.web.exception.ServiceExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {
        ServiceExceptionHandlerTest.TestController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {ServiceExceptionHandler.class,
        ServiceExceptionHandlerTest.TestController.class, ErrorManager.class})
class ServiceExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    private static MemoryAppender memoryAppender;

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

    @RestController
    @Slf4j
    static class TestController {
        @GetMapping("/test")
        String test() {
            throw new ServiceException("DUMMY_CODE", "DUMMY_MESSAGE");
        }
    }

    @Test
    void testSimpleException() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().json("{\"code\":\"DUMMY_CODE\",\"message\":\"DUMMY_MESSAGE\"}", false));


        ErrorManagerTest.checkStackTraceSuppressedLog(memoryAppender, "A ServiceException occurred handling request GET /test: HttpStatus 500 INTERNAL_SERVER_ERROR - DUMMY_CODE: DUMMY_MESSAGE at it.gov.pagopa.common.web.exception.ServiceExceptionHandlerTest\\$TestController.test\\(ServiceExceptionHandlerTest.java:[0-9]+\\)");
    }

}
