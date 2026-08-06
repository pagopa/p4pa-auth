package it.gov.pagopa.payhub.auth.exception.transcoder.handler;

import it.gov.pagopa.payhub.auth.exception.common.BaseBusinessException;
import it.gov.pagopa.payhub.auth.exception.common.InvalidValueException;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class BaseBusinessExceptionMessageTranscoderTest {

  private final BaseBusinessExceptionMessageTranscoder transcoder = new BaseBusinessExceptionMessageTranscoder();

  @Test
  void testTranscode() {
    // Given
    BaseBusinessException businessException = new InvalidValueException("code", "message", new ArrayList<>());

    // When
    ExceptionMessageTranscoded result = transcoder.transcode(businessException);

    // Then
    Assertions.assertEquals(
      new ExceptionMessageTranscoded(businessException.getCode(), businessException.getMessage(), businessException.getFields()),
      result);
  }
}
