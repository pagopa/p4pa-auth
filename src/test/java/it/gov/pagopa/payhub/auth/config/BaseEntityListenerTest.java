package it.gov.pagopa.payhub.auth.config;

import it.gov.pagopa.payhub.auth.model.BaseEntity;
import it.gov.pagopa.payhub.auth.utils.SecurityUtilsTest;
import it.gov.pagopa.payhub.auth.utils.UtilitiesTest;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;

class BaseEntityListenerTest {

    private final BaseEntityListener listener = new BaseEntityListener();

    private final String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
    private final String traceId = "TRACEID";

    @BeforeEach
    void configureContext() {
        UserInfo userInfo = new UserInfo();
        userInfo.setMappedExternalUserId(mappedExternalUserId);
        SecurityUtilsTest.configureSecurityContext(userInfo, "token");

        UtilitiesTest.setTraceId(traceId);
    }

    @AfterEach
    void clear() {
        SecurityUtilsTest.clearSecurityContext();
        UtilitiesTest.clearTraceIdContext();
    }

    private static class TestBaseEntity extends BaseEntity {
    }

    @Test
    void givenNoCreationDateWhenOnBeforeConvertThenFillTechFields() {
        // Given
        TestBaseEntity baseEntity = new TestBaseEntity();
        baseEntity.setUpdateDate(LocalDateTime.now().minusDays(1));

        LocalDateTime before = LocalDateTime.now();

        // When
        listener.onBeforeConvert(new BeforeConvertEvent<>(baseEntity, "dummy"));

        // Then
        Assertions.assertTrue(before.isBefore(baseEntity.getCreationDate()));
        Assertions.assertTrue(before.isBefore(baseEntity.getUpdateDate()));
        Assertions.assertEquals(mappedExternalUserId, baseEntity.getUpdateOperatorExternalId());
        Assertions.assertEquals(traceId, baseEntity.getUpdateTraceId());
    }

    @Test
    void givenCreationDateWhenOnBeforeConvertThenFillTechFields() {
        // Given
        LocalDateTime creationDate = LocalDateTime.now().minusDays(1);
        TestBaseEntity baseEntity = new TestBaseEntity();
        baseEntity.setCreationDate(creationDate);
        baseEntity.setUpdateDate(creationDate);

        LocalDateTime before = LocalDateTime.now();

        // When
        listener.onBeforeConvert(new BeforeConvertEvent<>(baseEntity, "dummy"));

        // Then
        Assertions.assertSame(creationDate, baseEntity.getCreationDate());
        Assertions.assertTrue(before.isBefore(baseEntity.getUpdateDate()));
        Assertions.assertEquals(mappedExternalUserId, baseEntity.getUpdateOperatorExternalId());
        Assertions.assertEquals(traceId, baseEntity.getUpdateTraceId());
    }

    @Test
    void whenSetTechFieldsOnDocumentUpdateThenConfigureTechFields() {
        // Given
        Update documentMock = Mockito.mock(Update.class);
        LocalDateTime before = LocalDateTime.now();

        Mockito.when(documentMock.setOnInsert(Mockito.anyString(), Mockito.any())).thenReturn(documentMock);
        Mockito.when(documentMock.set(Mockito.anyString(), Mockito.any())).thenReturn(documentMock);

        // When
        Update result = BaseEntityListener.setTechFieldsOnDocumentUpdate(documentMock);

        // Then
        Assertions.assertSame(result, documentMock);
        Mockito.verify(documentMock).setOnInsert(Mockito.eq(BaseEntity.Fields.creationDate), Mockito.argThat(before::isBefore));
        Mockito.verify(documentMock).set(Mockito.eq(BaseEntity.Fields.updateDate), Mockito.argThat(before::isBefore));
        Mockito.verify(documentMock).set(BaseEntity.Fields.updateOperatorExternalId, mappedExternalUserId);
        Mockito.verify(documentMock).set(BaseEntity.Fields.updateTraceId, traceId);
    }
}
