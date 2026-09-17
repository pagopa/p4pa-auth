package it.gov.pagopa.payhub.auth.connector.debtposition;

public interface DebtPositionTypeOrgOperatorService {
    void relateUserToDefaultDPTypeOrg(String operatorExternalUserId, Long organizationId, String fiscalCode, String accessToken);
}