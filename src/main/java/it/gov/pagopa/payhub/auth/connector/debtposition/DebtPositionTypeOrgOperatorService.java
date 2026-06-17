package it.gov.pagopa.payhub.auth.connector.debtposition;

public interface DebtPositionTypeOrgOperatorService {
    void saveDefaultTechnicalDebtPositionTypeOrgForOperator(String operatorExternalUserId, Long organizationId, String accessToken);
}