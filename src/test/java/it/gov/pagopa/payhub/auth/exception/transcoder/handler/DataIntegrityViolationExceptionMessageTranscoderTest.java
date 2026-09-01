package it.gov.pagopa.payhub.auth.exception.transcoder.handler;

import com.mongodb.WriteConcernResult;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoActionOperation;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;

class DataIntegrityViolationExceptionMessageTranscoderTest {

  private final DataIntegrityViolationExceptionMessageTranscoder transcoder = new DataIntegrityViolationExceptionMessageTranscoder();

  @Test
  void testTranscode() {
    // Given
    DataIntegrityViolationException exception = new DataIntegrityViolationException("message");

    // When
    ExceptionMessageTranscoded result = transcoder.transcode(exception);

    // Then
    Assertions.assertEquals(
      new ExceptionMessageTranscoded(
        "AUTH_CONFLICT",
        "Conflict.",
        null),
      result);
  }

  @Test
  void givenMongoConstraintViolationExceptionCauseWhenTranscodeThenOk() {
    // Given
    DataIntegrityViolationException exception = new DataIntegrityViolationException("message", new MongoDataIntegrityViolationException("mongoErrorMessage", WriteConcernResult.unacknowledged(), MongoActionOperation.INSERT));

    // When
    ExceptionMessageTranscoded result = transcoder.transcode(exception);

    // Then
    Assertions.assertEquals(
      new ExceptionMessageTranscoded(
        "AUTH_CONFLICT",
        "Conflict. mongoErrorMessage",
        null),
      result);
  }
}
