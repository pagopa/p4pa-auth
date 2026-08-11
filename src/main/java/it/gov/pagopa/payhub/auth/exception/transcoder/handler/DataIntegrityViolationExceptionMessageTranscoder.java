package it.gov.pagopa.payhub.auth.exception.transcoder.handler;

import it.gov.pagopa.payhub.dto.generated.AuthErrorDTO;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;

public class DataIntegrityViolationExceptionMessageTranscoder implements ExceptionMessageTranscoder<DataIntegrityViolationException> {

  @Override
  public ExceptionMessageTranscoded transcode(DataIntegrityViolationException dataIntegrityViolationException) {
    String errorMsg = "Conflict.";
    if(dataIntegrityViolationException.getCause() instanceof MongoDataIntegrityViolationException mongoDataIntegrityViolationException) {
      errorMsg += " " + mongoDataIntegrityViolationException.getMostSpecificCause().getMessage();
    }
    return new ExceptionMessageTranscoded(
      AuthErrorDTO.ErrorEnum.AUTH_CONFLICT.getValue(),
      errorMsg,
      null) ;
  }
}
