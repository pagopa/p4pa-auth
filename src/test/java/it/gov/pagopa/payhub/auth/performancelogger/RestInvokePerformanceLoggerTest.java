package it.gov.pagopa.payhub.auth.performancelogger;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.payhub.auth.utils.MemoryAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

@ExtendWith(MockitoExtension.class)
class RestInvokePerformanceLoggerTest {
    public static final String APPENDER_NAME = "REST_INVOKE";

    @Mock
    private HttpRequest httpRequestMock;
    private final byte[] bodyMock = new byte[0];
    @Mock
    private ClientHttpRequestExecution requestExecutionMock;

    private MemoryAppender memoryAppender;

    private RestInvokePerformanceLogger filter;

    @BeforeEach
    void init() {
        filter = new RestInvokePerformanceLogger();
    }

    @BeforeEach
    public void setupMemoryAppender() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("PERFORMANCE_LOG."+APPENDER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                httpRequestMock,
                requestExecutionMock
        );
    }

    @Test
    void givenCoveredPathWhenDoFilterThenDontPerformanceLog() throws IOException {
        // Given
        ClientHttpResponse expectedResult = Mockito.mock(ClientHttpResponse.class);
        Mockito.when(expectedResult.getStatusCode()).thenReturn(HttpStatus.OK);

        Mockito.when(requestExecutionMock.execute(Mockito.same(httpRequestMock), Mockito.same(bodyMock)))
                .thenReturn(expectedResult);

        Mockito.when(httpRequestMock.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(httpRequestMock.getURI()).thenReturn(URI.create("/api/test"));

        // When
        ClientHttpResponse result = filter.intercept(httpRequestMock, bodyMock, requestExecutionMock);

        // Then
        PerformanceLoggerTest.assertPerformanceLogMessage(APPENDER_NAME, "GET /api/test", "HttpStatus: 200", memoryAppender);

        Assertions.assertSame(expectedResult, result);
    }

}
