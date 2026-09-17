package it.gov.pagopa.payhub.auth.connector.debtposition;

import it.gov.pagopa.payhub.auth.connector.debtposition.client.DebtPositionTypeOrgOperatorClient;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionTypeOrgOperatorServiceImpl implements DebtPositionTypeOrgOperatorService {

    private final DebtPositionTypeOrgOperatorClient debtPositionTypeOrgOperatorClient;

    public DebtPositionTypeOrgOperatorServiceImpl(DebtPositionTypeOrgOperatorClient debtPositionTypeOrgOperatorClient) {
        this.debtPositionTypeOrgOperatorClient = debtPositionTypeOrgOperatorClient;
    }

    @Override
    public void relateUserToDefaultDPTypeOrg(String operatorExternalUserId, Long organizationId, String fiscalCode, String accessToken) {
        debtPositionTypeOrgOperatorClient.relateUserToDefaultDPTypeOrg(
                operatorExternalUserId,
                organizationId,
                fiscalCode,
                accessToken
        );
    }
}
