package it.gov.pagopa.payhub.auth.connector.debtposition.client;

import it.gov.pagopa.payhub.auth.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgOperatorsApi;
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
    void whenGetOrganizationByIpaCodeThenInvokeWithAccessToken() {
        //GIVEN
        String operatorExternalUserId = "operatorExternalUserId";
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgOperatorsApi(accessToken))
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
        Mockito.verify(debtPositionTypeOrgOperatorsApiMock)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(
                        operatorExternalUserId,
                        organizationId
                );
    }

    @Test
    void givenConflictWhenGetOrganizationByIpaCodeThenDoNothing() {
        //GIVEN
        String operatorExternalUserId = "operatorExternalUserId";
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException conflictException = HttpClientErrorException.create(HttpStatus.CONFLICT, "Conflict", null, null, null);

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgOperatorsApi(accessToken))
                .thenReturn(debtPositionTypeOrgOperatorsApiMock);
        Mockito.doThrow(conflictException)
                .when(debtPositionTypeOrgOperatorsApiMock)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(
                        operatorExternalUserId,
                        organizationId
                );

        //WHEN,THEN
        Assertions.assertDoesNotThrow(
                () -> client.saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                        operatorExternalUserId,
                        organizationId,
                        accessToken
                )
        );
    }

    @Test
    void givenGenericHttpExceptionWhenGetOrganizationByIpaCodeThenThrowIt() {
        //GIVEN
        String operatorExternalUserId = "operatorExternalUserId";
        Long organizationId = 1L;
        String accessToken = "ACCESSTOKEN";
        HttpClientErrorException expectedException = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgOperatorsApi(accessToken))
                .thenReturn(debtPositionTypeOrgOperatorsApiMock);
        Mockito.doThrow(expectedException)
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
