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
@Table(name = "mygov_operatore")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class MyPivotOperator {

  @Id
  @SequenceGenerator(name = "myPivotOperatoreSeq", sequenceName = "mygov_operatore_mygov_operatore_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "myPivotOperatoreSeq")
  private Long mygovOperatoreId;
  private String ruolo;
  private String codFedUserId;
  private String codIpaEnte;

}
