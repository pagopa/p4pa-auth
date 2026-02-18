package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {
  private LocalDateTime creationDate;
  private LocalDateTime updateDate;
  private String updateOperatorExternalId;
  private String updateTraceId;

}


