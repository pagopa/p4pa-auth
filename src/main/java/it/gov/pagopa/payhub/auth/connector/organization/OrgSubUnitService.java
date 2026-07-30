package it.gov.pagopa.payhub.auth.connector.organization;

import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;

import java.util.List;

public interface OrgSubUnitService {

    List<OrgSubUnit> getActiveOrgSubUnitsByOrganizationId(Long organizationId, String accessToken);

    List<OrgSubUnit> getActiveOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(Long organizationId, String operatorExternalUserId, String accessToken);
}