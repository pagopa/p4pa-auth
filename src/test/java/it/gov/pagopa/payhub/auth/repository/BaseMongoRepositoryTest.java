package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.config.BaseEntityListener;
import it.gov.pagopa.payhub.auth.model.BaseEntity;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
abstract class BaseMongoRepositoryTest {

    @Mock
    protected MongoTemplate mongoTemplateMock;

    /**
     * it will assert the right configuration of mongo update operation.<BR />In case of mongoTemplateMock configuration on tests, it will be necessary the usage of the Mockito.do().when() form
     */
    @SuppressWarnings("unchecked")
    @AfterEach
    void assertSetTechFieldsOnDocumentUpdateConfiguration() {
        ArgumentMatcher<UpdateDefinition> matchSetTechFieldsOnDocumentUpdateConfiguration = u -> {
            Assertions.assertTrue(argSetTechFieldsOnDocumentUpdateInvoke((Update) u).matches((Update) u), "Tech fields not set! Has BaseEntityListener.setTechFieldsOnDocumentUpdate been called on configured Update object?");
            return true;
        };

        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateFirst(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateFirst(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class), Mockito.anyString()));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateFirst(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(String.class)));

        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateMulti(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateMulti(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class), Mockito.anyString()));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.updateMulti(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(String.class)));

        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.upsert(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.upsert(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class), Mockito.anyString()));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.upsert(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(String.class)));

        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.findAndModify(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.findAndModify(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(Class.class), Mockito.any(String.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.findAndModify(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(FindAndModifyOptions.class), Mockito.any(Class.class)));
        verifySetTechFieldsOnDocumentUpdateConfiguration(mock -> mock.findAndModify(Mockito.any(), Mockito.argThat(matchSetTechFieldsOnDocumentUpdateConfiguration), Mockito.any(FindAndModifyOptions.class), Mockito.any(Class.class), Mockito.any(String.class)));
    }

    private void verifySetTechFieldsOnDocumentUpdateConfiguration(Consumer<MongoTemplate> updateMethodCheck) {
        try {
            updateMethodCheck.accept(Mockito.verify(mongoTemplateMock));
        } catch (WantedButNotInvoked e){
            // Not invoked exception not interested
        } catch (Throwable e) {
            Assertions.fail("Tech fields not set! Has BaseEntityListener.setTechFieldsOnDocumentUpdate been called on configured Update object?");
        }
    }

    public static ArgumentMatcher<Update> argSetTechFieldsOnDocumentUpdateInvoke(Update update) {
        return u -> {
            LocalDateTime now = u.getUpdateObject().get("$setOnInsert", Document.class).get(BaseEntity.Fields.creationDate, LocalDateTime.class);
            Assertions.assertNotNull(now);
            Assertions.assertFalse(now.isAfter(LocalDateTime.now()));
            Assertions.assertTrue(now.isAfter(LocalDateTime.now().minusMinutes(1)));

            Update wrap = BaseEntityListener.setTechFieldsOnDocumentUpdate(update);
            wrap.setOnInsert(BaseEntity.Fields.creationDate, now)
                    .set(BaseEntity.Fields.updateDate, now);

            u.setOnInsert(BaseEntity.Fields.creationDate, now)
                    .set(BaseEntity.Fields.updateDate, now);
            return wrap.equals(u);
        };
    }
}
