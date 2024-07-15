package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthzService {
    Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest);
    void deleteOrganizationOperator(String organizationIpaCode, String operatorId);
}
