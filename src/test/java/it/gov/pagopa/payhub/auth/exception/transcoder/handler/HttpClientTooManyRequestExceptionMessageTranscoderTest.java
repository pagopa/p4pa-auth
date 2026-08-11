package it.gov.pagopa.payhub.auth.exception.transcoder.handler;

import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.Mockito.mock;

class HttpClientTooManyRequestExceptionMessageTranscoderTest {

  private final HttpClientTooManyRequestExceptionMessageTranscoder transcoder = new HttpClientTooManyRequestExceptionMessageTranscoder();

  @Test
  void testTranscode() {
    // Given
    HttpClientErrorException.TooManyRequests exception = mock(HttpClientErrorException.TooManyRequests.class);

    // When
    ExceptionMessageTranscoded result = transcoder.transcode(exception);

    // Then
    Assertions.assertEquals(
      new ExceptionMessageTranscoded(
        "AUTH_TOO_MANY_REQUESTS",
        exception.getMessage(),
        null),
      result
    );
  }
}
