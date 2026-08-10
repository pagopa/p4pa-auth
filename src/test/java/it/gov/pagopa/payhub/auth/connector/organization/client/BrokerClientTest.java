package it.gov.pagopa.payhub.auth.connector.organization.client;

import it.gov.pagopa.payhub.auth.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.payhub.auth.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

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

        when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
                .thenReturn(brokerEntityControllerApi);
        when(brokerEntityControllerApi.crudGetBroker(String.valueOf(brokerId)))
                .thenReturn(expectedResult);

        Broker result = brokerClient.getBrokerById(brokerId, accessToken);

        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNoExistentIpaCodeWhenGetOrganizationByIpaCodeThenNull() {
        Long brokerId = 1L;
        String accessToken = "ACCESSTOKEN";

        when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
                .thenReturn(brokerEntityControllerApi);
        when(brokerEntityControllerApi.crudGetBroker(String.valueOf(brokerId)))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "NOTFOUND", "NOTFOUND", "Not found"));

        Broker result = brokerClient.getBrokerById(brokerId, accessToken);

        Assertions.assertNull(result);
    }

}
