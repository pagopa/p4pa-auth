package it.gov.pagopa.payhub.auth.config;

import it.gov.pagopa.payhub.auth.model.BaseEntity;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.auth.utils.Utilities;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseEntityListener extends AbstractMongoEventListener<BaseEntity> {

  @Override
  public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
    onSave(event.getSource());
  }

  private void onSave(BaseEntity entity) {
    LocalDateTime now = LocalDateTime.now();
    if(entity.getCreationDate() == null) {
      entity.setCreationDate(now);
    }
    entity.setUpdateDate(now);
    entity.setUpdateOperatorExternalId(SecurityUtils.getCurrentUserExternalId());
    entity.setUpdateTraceId(Utilities.getTraceId());
  }

  public static Update setTechFieldsOnDocumentUpdate(Update document) {
    LocalDateTime now = LocalDateTime.now();
    return document
      .setOnInsert(BaseEntity.Fields.creationDate, now)
      .set(BaseEntity.Fields.updateDate, now)
      .set(BaseEntity.Fields.updateOperatorExternalId, SecurityUtils.getCurrentUserExternalId())
      .set(BaseEntity.Fields.updateTraceId, Utilities.getTraceId());
  }
}
