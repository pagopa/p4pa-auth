package it.gov.pagopa.payhub.auth.connector.debtposition.client;

import it.gov.pagopa.payhub.auth.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.RelateUserToDefaultDPTypeOrgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DebtPositionTypeOrgOperatorClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeOrgOperatorClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public void relateUserToDefaultDPTypeOrg(String operatorExternalUserId, Long organizationId, String fiscalCode, String accessToken) {
        RelateUserToDefaultDPTypeOrgDTO relateUserToDefaultDPTypeOrgDTO = RelateUserToDefaultDPTypeOrgDTO.builder()
                .operatorExternalUserId(operatorExternalUserId)
                .organizationId(organizationId)
                .fiscalCode(fiscalCode)
                .build();
        debtPositionApisHolder.getDebtPositionTypeOrgOperatorsApi(accessToken)
                .relateUserToDefaultDPTypeOrg(relateUserToDefaultDPTypeOrgDTO);
    }

}
