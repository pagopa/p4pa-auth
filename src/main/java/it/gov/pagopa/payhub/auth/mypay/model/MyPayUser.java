package it.gov.pagopa.payhub.auth.mypay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;
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
public class MyPayUser {

  @Id
  @SequenceGenerator(name = "myPayUserSeq", sequenceName = "mygov_utente_mygov_utente_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "myPayUserSeq")
  private Long mygovUtenteId;
  private String codFedUserId;
  private String codCodiceFiscaleUtente;
  private String deEmailAddress;
  private String deFirstname;
  private String deLastname;
  private String deFedLegalEntity;
  private Date dtUltimoLogin;
  private String indirizzo;
  private String civico;
  private String cap;
  private Long comuneId;
  private Long provinciaId;
  private Long nazioneId;
  private Date dtSetAddress;
  private String deEmailAddressNew;

}
