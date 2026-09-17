package it.gov.pagopa.payhub.auth.connector.debtposition;

import it.gov.pagopa.payhub.auth.connector.debtposition.client.DebtPositionTypeOrgOperatorClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

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
    void whenRelateUserToDefaultDPTypeOrgThenInvokeClient(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String operatorExternalUserId = "OPERATOR_EXTERNAL_USER_ID";
        Long organizationId = 1L;
        String fiscalCode = "fiscalCode";

        doNothing()
                .when(debtPositionTypeOrgOperatorClientMock)
                .relateUserToDefaultDPTypeOrg(operatorExternalUserId, organizationId, fiscalCode, accessToken);

        // When
        service.relateUserToDefaultDPTypeOrg(operatorExternalUserId, organizationId, fiscalCode, accessToken);

        // Then
        verify(debtPositionTypeOrgOperatorClientMock)
                .relateUserToDefaultDPTypeOrg(operatorExternalUserId, organizationId, fiscalCode, accessToken);
    }

}
