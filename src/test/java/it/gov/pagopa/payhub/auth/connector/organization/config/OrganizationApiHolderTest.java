package it.gov.pagopa.payhub.auth.connector.organization.config;

import it.gov.pagopa.payhub.auth.config.json.JsonConfig;
import it.gov.pagopa.payhub.auth.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationDetailDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationApiHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private OrganizationApisHolder organizationApisHolder;
    private OrganizationApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = OrganizationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();

        organizationApisHolder = new OrganizationApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(organizationApisHolder.getOrganizationSearchControllerApi(null));
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
                accessToken -> organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                        .crudOrganizationsFindByIpaCode("IPACODE"),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetOrganizationSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                        .crudOrganizationsFindByIpaCode("IPACODE"),
                new ParameterizedTypeReference<>() {},
                organizationApisHolder::unload);
    }

    @Test
    void whenGetAuthnApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> organizationApisHolder.getBrokerEntityControllerApi(accessToken)
                        .crudGetBroker("BROKERID"),
                new ParameterizedTypeReference<>() {},
                organizationApisHolder::unload);
    }

    @Test
    void whenGetOrganizationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken ->{
                    organizationApisHolder.getOrganizationApi(accessToken)
                            .updateOrganization(new OrganizationDetailDTO());
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                organizationApisHolder::unload);
    }

}