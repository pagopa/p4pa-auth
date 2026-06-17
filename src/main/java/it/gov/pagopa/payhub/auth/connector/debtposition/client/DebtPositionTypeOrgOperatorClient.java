package it.gov.pagopa.payhub.auth.connector.debtposition.client;

import it.gov.pagopa.payhub.auth.connector.debtposition.config.DebtPositionApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DebtPositionTypeOrgOperatorClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeOrgOperatorClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public void saveDefaultTechnicalDebtPositionTypeOrgForOperator(String operatorExternalUserId, Long organizationId, String accessToken) {
        debtPositionApisHolder.getDebtPositionTypeOrgOperatorsApi(accessToken)
                .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(operatorExternalUserId, organizationId);
    }

}
