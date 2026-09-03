package it.gov.pagopa.payhub.auth.exception.transcoder.handler;

import it.gov.pagopa.payhub.dto.generated.ErrorFieldDTO;
import it.gov.pagopa.payhub.dto.generated.AuthErrorDTO;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.payhub.auth.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodArgumentTypeMismatchExceptionMessageTranscoder implements ExceptionMessageTranscoder<MethodArgumentTypeMismatchException> {

  @Override
  public ExceptionMessageTranscoded transcode(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
    List<ErrorFieldDTO> errorFields = List.of(
            ErrorFieldDTO.builder()
              .field(methodArgumentTypeMismatchException.getName())
              .error("InvalidValue")
              .message("Cannot convert value '" + methodArgumentTypeMismatchException.getValue() + "' to required type '" + Objects.requireNonNullElse(methodArgumentTypeMismatchException.getRequiredType(), String.class).getSimpleName() + "'")
              .build()
            );

    String errorDescription = errorFields.stream()
      .map(e -> " " + e.getField() + ": " + e.getMessage())
      .collect(Collectors.joining(";"));

    return new ExceptionMessageTranscoded(
      AuthErrorDTO.ErrorEnum.INVALID_REQUEST.getValue(),
      "Invalid request parameter." + errorDescription,
      errorFields
    );
  }
}
