package it.gov.pagopa.payhub.auth.connector.debtposition.client;

import it.gov.pagopa.payhub.auth.connector.debtposition.config.DebtPositionApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class DebtPositionTypeOrgOperatorClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeOrgOperatorClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public void saveDefaultTechnicalDebtPositionTypeOrgForOperator(String operatorExternalUserId, Long organizationId, String accessToken) {
        try {
            debtPositionApisHolder.getDebtPositionTypeOrgOperatorsApi(accessToken)
                    .saveDefaultTechnicalDebtPositionTypeOrgOperatorsForOperator(operatorExternalUserId, organizationId);
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Default technical DebtPositionTypeOrgs already assigned to operator with operatorExternalUserId {} and organizationId {}", operatorExternalUserId, organizationId);
        }
    }

}
