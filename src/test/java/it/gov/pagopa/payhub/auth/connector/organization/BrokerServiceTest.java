package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.payhub.auth.connector.organization.client.BrokerClient;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrokerServiceTest {

    @Mock
    private BrokerClient brokerClientMock;

    private BrokerService service;

    @BeforeEach
    void init(){
        service = new BrokerServiceImpl(brokerClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                brokerClientMock
        );
    }

    @Test
    void whenGetBrokerByIdThenInvokeClient(){
        // Given
        String accessToken = "ACCESSTOKEN";
        long brokerId = 1L;
        Broker expectedResult = new Broker();

        Mockito.when(brokerClientMock.getBrokerById(brokerId, accessToken))
                .thenReturn(expectedResult);

        // When
        Broker result = service.getBrokerById(brokerId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

}
