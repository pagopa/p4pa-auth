package it.gov.pagopa.payhub.auth.performancelogger;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.payhub.auth.utils.MemoryAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    private ServletRequest httpServletRequestMock;
    @Mock
    private ServletResponse servletResponseMock;
    @Mock
    private FilterChain filterChainMock;

    private MemoryAppender memoryAppender;

    private ApiRequestPerformanceLogger filter;

    @BeforeEach
    void init() {
        httpServletRequestMock = Mockito.mock(HttpServletRequest.class);
        filter = new ApiRequestPerformanceLogger();
    }

    @BeforeEach
    public void setupMemoryAppender() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("PERFORMANCE_LOG.API_REQUEST");
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    void verifyNoMoreInteractions() throws ServletException, IOException {
        Mockito.verify(filterChainMock)
                .doFilter(httpServletRequestMock, servletResponseMock);

        Mockito.verifyNoMoreInteractions(
                httpServletRequestMock,
                servletResponseMock,
                filterChainMock
        );
    }

    @Test
    void givenNotHttpServletRequestWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        httpServletRequestMock = Mockito.mock(ServletRequest.class);

        // When
        filter.doFilter(httpServletRequestMock, servletResponseMock, filterChainMock);

        // Then
        Assertions.assertEquals(0, memoryAppender.getLoggedEvents().size());
    }

    @Test
    void givenNotCoveredPathWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        configureRequestPath("/actuator");

        // When
        filter.doFilter(httpServletRequestMock, servletResponseMock, filterChainMock);

        // Then
        Assertions.assertEquals(0, memoryAppender.getLoggedEvents().size());
    }

    @Test
    void givenCoveredPathWhenDoFilterThenDontPerformanceLog() throws ServletException, IOException {
        // Given
        configureRequestPath("/api/test");

        // When
        filter.doFilter(httpServletRequestMock, servletResponseMock, filterChainMock);

        // Then
        PerformanceLoggerTest.assertPerformanceLogMessage("API_REQUEST", "GET /api/test", "", memoryAppender);

        Mockito.verify(((HttpServletRequest)httpServletRequestMock), Mockito.times(2))
                .getRequestURI();
        Mockito.verify(((HttpServletRequest)httpServletRequestMock), Mockito.times(1))
                .getMethod();
    }

    private void configureRequestPath(String path) {
        Mockito.when(((HttpServletRequest)httpServletRequestMock).getRequestURI())
                .thenReturn(path);
        Mockito.lenient().when(((HttpServletRequest) httpServletRequestMock).getMethod())
                .thenReturn("GET");
    }

}
