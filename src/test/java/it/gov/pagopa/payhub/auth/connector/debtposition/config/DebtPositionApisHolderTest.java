package it.gov.pagopa.payhub.auth.connector.debtposition.config;

import it.gov.pagopa.payhub.auth.config.json.JsonConfig;
import it.gov.pagopa.payhub.auth.connector.BaseApiHolderTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private DebtPositionApisHolder apisHolder;
    private DebtPositionApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = DebtPositionApiClientConfig.builder()
                .baseUrl("http://example.com")
                .printBodyWhenError(true)
                .maxAttempts(3)
                .build();

        apisHolder = new DebtPositionApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getDebtPositionTypeOrgOperatorsApi(null));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void testRetryConfiguration() {
        assertRetry(apiClientConfig,
                accessToken -> apisHolder.getDebtPositionTypeOrgOperatorsApi(accessToken)
                        .deleteOperators(1L, Set.of("operatorExternalUserId")),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetDebtPositionTypeOrgOperatorsApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionTypeOrgOperatorsApi(accessToken)
                        .deleteOperators(1L, Set.of("operatorExternalUserId")),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
        verify(restTemplateMock).setErrorHandler(Mockito.any(ResponseErrorHandler.class));
    }

}