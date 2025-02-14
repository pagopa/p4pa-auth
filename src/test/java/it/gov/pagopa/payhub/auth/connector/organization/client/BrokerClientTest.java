package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.p4pa_organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class BrokerClientTest {
    @Mock
    private OrganizationApisHolder organizationApisHolder;
    @Mock
    private BrokerEntityControllerApi brokerEntityControllerApi;

    private BrokerClient brokerClient;

    @BeforeEach
    void setUp() {
        brokerClient = new BrokerClient(organizationApisHolder);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationApisHolder
        );
    }

    @Test
    void whenGetOrganizationByIpaCodeThenInvokeWithAccessToken() {
        Long brokerId = 1L;
        String accessToken = "ACCESSTOKEN";
        Broker expectedResult = new Broker();

        Mockito.when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
                .thenReturn(brokerEntityControllerApi);
        Mockito.when(brokerEntityControllerApi.crudGetBroker(String.valueOf(brokerId)))
                .thenReturn(expectedResult);

        Broker result = brokerClient.getBrokerById(brokerId, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNoExistentIpaCodeWhenGetOrganizationByIpaCodeThenNull() {
        Long brokerId = 1L;
        String accessToken = "ACCESSTOKEN";

        Mockito.when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
                .thenReturn(brokerEntityControllerApi);
        Mockito.when(brokerEntityControllerApi.crudGetBroker(String.valueOf(brokerId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        Broker result = brokerClient.getBrokerById(brokerId, accessToken);

        Assertions.assertNull(result);
    }

}
