package it.gov.pagopa.payhub.auth.connector.organization.config;

import it.gov.pagopa.payhub.auth.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrganizationDetailDTO;
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

@ExtendWith(MockitoExtension.class)
class OrganizationApiHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private OrganizationApisHolder organizationApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        OrganizationApiClientConfig clientConfig = OrganizationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        organizationApisHolder = new OrganizationApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
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