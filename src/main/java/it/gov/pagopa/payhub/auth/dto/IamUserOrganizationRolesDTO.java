package it.gov.pagopa.payhub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class IamUserOrganizationRolesDTO {

  private String organizationIpaCode;
  private List<String> roles = new ArrayList<>();

}

