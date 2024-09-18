package it.gov.pagopa.payhub.auth.mypivot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Table(name = "mygov_utente")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class MyPivotUser {

  @Id
  @SequenceGenerator(name = "myPivotUserSeq", sequenceName = "mygov_utente_mygov_utente_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "myPivotUserSeq")
  private Long mygovUtenteId;
  private String codFedUserId;
}
