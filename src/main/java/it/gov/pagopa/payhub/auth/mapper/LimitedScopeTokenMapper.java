package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.dto.generated.*;
import org.springframework.stereotype.Component;

@Component
public class LimitedScopeTokenMapper {

    public LimitedScopeResource mapRequestToLimitedScopeResource(LimitedTokenRequest request, UserOrganizationRoles organization) {
        return LimitedScopeResource.builder()
                .app(request.getApp())
                .organization(organization)
                .resource(request.getResource())
                .resourceId(request.getResourceId())
                .singleUsage(request.getSingleUsage())
                .sessionData(request.getSessionData())
                .build();
    }

    public IamUserInfoDTO mapBaseUserInfoToIamUserInfoDTO(
            UserInfo userInfo,
            LimitedTokenRequest request
    ) {
        UserOrganizationRoles organization = userInfo.getOrganizations().stream()
                .filter(org -> org.getOrganizationId().equals(request.getOrganizationId()))
                .findFirst()
                .orElseThrow(() -> new UserUnauthorizedException("User not allowed on organization " + request.getOrganizationId()));

        return IamUserInfoDTO.builder()
                .type(UserInfoLimitedScope.class.getSimpleName())
                .traceId(userInfo.getTraceId())
                .userId(userInfo.getUserId())
                .fiscalCode(userInfo.getFiscalCode())
                .familyName(userInfo.getFamilyName())
                .name(userInfo.getName())
                .issuer(userInfo.getIssuer())
                .resource(this.mapRequestToLimitedScopeResource(request, organization))
                .innerUserId(userInfo.getUserId())
                .mappedExternalUserId(userInfo.getMappedExternalUserId())
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode(organization.getOrganizationIpaCode())
                        .roles(organization.getRoles())
                        .build())
                .systemUser(Boolean.TRUE.equals(userInfo.getSystemUser()))
                .build();
    }

}
