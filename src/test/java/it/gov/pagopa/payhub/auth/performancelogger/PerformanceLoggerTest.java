package it.gov.pagopa.payhub.auth.performancelogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import it.gov.pagopa.payhub.auth.utils.MemoryAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PerformanceLoggerTest {

    @Test
    void testThresholdLevelTranscoding(){
        //TODO
    }

    public static void assertPerformanceLogMessage(String expectedAppenderName, String expectedContextData, String expectedPayload, MemoryAppender memoryAppender) {
        Assertions.assertEquals(1, memoryAppender.getLoggedEvents().size());
        ILoggingEvent event = memoryAppender.getLoggedEvents().getFirst();
        Assertions.assertEquals("PERFORMANCE_LOG." + expectedAppenderName, event.getLoggerName());
        String logMessage = event.getFormattedMessage();
        Assertions.assertTrue(
                logMessage.matches(
                        "\\[%s] Time occurred to perform business logic: \\d+ ms\\. .*".formatted(expectedContextData)
                ) &&
                        logMessage.endsWith(". " + expectedPayload),
                "Unexpected logged message: " + logMessage
        );
    }
}
