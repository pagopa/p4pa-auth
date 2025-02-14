package it.gov.pagopa.payhub.auth.performancelogger;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.payhub.auth.utils.MemoryAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class ApiRequestPerformanceLoggerTest {
    public static final String APPENDER_NAME = "API_REQUEST";

    private ServletRequest httpServletRequestMock;
    private ServletResponse httpServletResponseMock;
    @Mock
    private FilterChain filterChainMock;

    private MemoryAppender memoryAppender;

    private ApiRequestPerformanceLogger filter;

    @BeforeEach
    void init() {
        httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        httpServletResponseMock = Mockito.mock(HttpServletResponse.class);
        filter = new ApiRequestPerformanceLogger();
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
    void verifyNoMoreInteractions() throws ServletException, IOException {
        Mockito.verify(filterChainMock)
                .doFilter(httpServletRequestMock, httpServletResponseMock);

        Mockito.verifyNoMoreInteractions(
                httpServletRequestMock,
                httpServletResponseMock,
                filterChainMock
        );
    }

    @Test
    void givenNotHttpServletRequestWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        httpServletRequestMock = Mockito.mock(ServletRequest.class);

        // When
        filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        // Then
        Assertions.assertEquals(0, memoryAppender.getLoggedEvents().size());
    }

    @Test
    void givenNotCoveredPathWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        configureRequestPath("/actuator");

        // When
        filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        // Then
        Assertions.assertEquals(0, memoryAppender.getLoggedEvents().size());
    }

    @Test
    void givenCoveredPathWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        configureRequestPath("/api/test");

        // When
        filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        // Then
        PerformanceLoggerTest.assertPerformanceLogMessage(APPENDER_NAME, "GET /api/test", "HttpStatus: 200", memoryAppender);

        Mockito.verify(((HttpServletRequest)httpServletRequestMock), Mockito.times(2))
                .getRequestURI();
        Mockito.verify(((HttpServletRequest)httpServletRequestMock), Mockito.times(1))
                .getMethod();
        Mockito.verify(((HttpServletResponse)httpServletResponseMock))
                .getStatus();
    }

    private void configureRequestPath(String path) {
        Mockito.when(((HttpServletRequest)httpServletRequestMock).getRequestURI())
                .thenReturn(path);
        Mockito.lenient().when(((HttpServletRequest) httpServletRequestMock).getMethod())
                .thenReturn("GET");
        Mockito.lenient().when(((HttpServletResponse)httpServletResponseMock).getStatus())
                .thenReturn(200);
    }

}
