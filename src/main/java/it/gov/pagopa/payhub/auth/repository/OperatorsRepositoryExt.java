package it.gov.pagopa.payhub.auth.repository;

import it.gov.pagopa.payhub.auth.model.Operator;

import java.util.Set;

public interface OperatorsRepositoryExt {
    Operator registerOperator(String userId, String organizationIpaCode, String email, Set<String> roles);
    void deleteOrganizationOperator( String organizationIpaCode, String mappedExternalUserId);
}
