package it.gov.pagopa.payhub.auth.config.rest;

import it.gov.pagopa.payhub.dto.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
