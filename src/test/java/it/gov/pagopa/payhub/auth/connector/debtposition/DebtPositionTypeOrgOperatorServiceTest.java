package it.gov.pagopa.payhub.auth.connector.debtposition;

import it.gov.pagopa.payhub.auth.connector.debtposition.client.DebtPositionTypeOrgOperatorClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgOperatorServiceTest {

    @Mock
    private DebtPositionTypeOrgOperatorClient debtPositionTypeOrgOperatorClientMock;

    private DebtPositionTypeOrgOperatorService service;

    @BeforeEach
    void init(){
        service = new DebtPositionTypeOrgOperatorServiceImpl(debtPositionTypeOrgOperatorClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgOperatorClientMock
        );
    }

    @Test
    void whenGetOrganizationByIpaCodeByIdThenInvokeClient(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String operatorExternalUserId = "OPERATOR_EXTERNAL_USER_ID";
        Long organizationId = 1L;

        Mockito.doNothing()
                .when(debtPositionTypeOrgOperatorClientMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(operatorExternalUserId, organizationId, accessToken);

        // When
        service.saveDefaultTechnicalDebtPositionTypeOrgForOperator(operatorExternalUserId, organizationId, accessToken);

        // Then
        Mockito.verify(debtPositionTypeOrgOperatorClientMock)
                .saveDefaultTechnicalDebtPositionTypeOrgForOperator(operatorExternalUserId, organizationId, accessToken);
    }

}
