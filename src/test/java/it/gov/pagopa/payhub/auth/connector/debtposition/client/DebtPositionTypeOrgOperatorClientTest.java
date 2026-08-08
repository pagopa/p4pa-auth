package it.gov.pagopa.payhub.auth.connector.debtposition.client;

import it.gov.pagopa.payhub.auth.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtpositions.client.generated.DebtPositionTypeOrgOperatorsApi;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgOperatorClientTest {
    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionTypeOrgOperatorsApi debtPositionTypeOrgOperatorsApiMock;

    private DebtPositionTypeOrgOperatorClient client;

    @BeforeEach
    void setUp() {
        client = new DebtPositionTypeOrgOperatorClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            debtPositionApisHolderMock,
            debtPositionTypeOrgOperatorsApiMock
        );
    }

    @Test
    void givenNoExceptionWhenSaveDefaultTechnicalDebtPositionTypeOrgForOperatorThenOk() {
        //GIVEN
        String operatorExternalUserId = "operatorExternalUserId";
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgOperatorsApi(accessToken))
                .thenReturn(debtPositionTypeOrgOperatorsApiMock);
        Mockito.doNothing()
                .when(debtPositionTypeOrgOperatorsApiMock)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(
                        operatorExternalUserId,
                        organizationId
                );
        //WHEN
        client.saveDefaultTechnicalDebtPositionTypeOrgForOperator(operatorExternalUserId, organizationId, accessToken);

        //THEN
        verify(debtPositionTypeOrgOperatorsApiMock)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(
                        operatorExternalUserId,
                        organizationId
                );
    }

    @Test
    void givenGenericHttpExceptionWhenSaveDefaultTechnicalDebtPositionTypeOrgForOperatorThenThrowIt() {
        //GIVEN
        String operatorExternalUserId = "operatorExternalUserId";
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException expectedException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgOperatorsApi(accessToken))
                .thenReturn(debtPositionTypeOrgOperatorsApiMock);
        doThrow(expectedException)
                .when(debtPositionTypeOrgOperatorsApiMock)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(
                        operatorExternalUserId,
                        organizationId
                );

        //WHEN
        HttpClientErrorException result = Assertions.assertThrows(
                expectedException.getClass(),
                () -> client.saveDefaultTechnicalDebtPositionTypeOrgForOperator(operatorExternalUserId, organizationId, accessToken)
        );

        //THEN
        Assertions.assertSame(expectedException, result);
    }

}
