package it.gov.pagopa.payhub.auth.dto;

import it.gov.pagopa.payhub.dto.generated.LimitedScopeResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class IamUserInfoDTO {
    private String type;
    private String traceId;
    private String userId;
    private String fiscalCode;
    private String familyName;
    private String name;
    private String issuer;
    private IamUserOrganizationRolesDTO organizationAccess;
    private LimitedScopeResource resource;

      // field calculated upon registration
      private String innerUserId;
      private String mappedExternalUserId;

      // field to check if it is a real user or a machine/system user
      private boolean systemUser;
}

