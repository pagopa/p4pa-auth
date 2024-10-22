package it.gov.pagopa.payhub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class IamUserInfoDTO {

  private String userId;
  private String fiscalCode;
  private String familyName;
  private String name;
  private String issuer;
  private IamUserOrganizationRolesDTO organizationAccess;

  // field calculated upon registration
  private String innerUserId;

  // field to check if it is a real user or a machine/system user
  private boolean systemUser;
}

