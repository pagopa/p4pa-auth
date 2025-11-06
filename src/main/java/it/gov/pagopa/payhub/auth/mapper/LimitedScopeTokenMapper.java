package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.dto.generated.BaseUserInfo;
import it.gov.pagopa.payhub.dto.generated.LimitedScopeResource;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import org.springframework.stereotype.Component;

@Component
public class LimitedScopeTokenMapper {

    public UserInfoLimitedScope mapBaseUserInfoToLimitedScope(BaseUserInfo baseUserInfo, LimitedTokenRequest request) {
        return UserInfoLimitedScope.builder()
        		.resource(mapRequestToLimitedScope(request))
        		.userId(baseUserInfo.getUserId())
        		.mappedExternalUserId(baseUserInfo.getMappedExternalUserId())
        		.fiscalCode(baseUserInfo.getFiscalCode())
        		.familyName(baseUserInfo.getFamilyName())
        		.name(baseUserInfo.getName())
        		.issuer(baseUserInfo.getIssuer())
        		.organizationAccess(baseUserInfo.getOrganizationAccess())
        		.organizations(baseUserInfo.getOrganizations())
        		.brokerId(baseUserInfo.getBrokerId())
        		.brokerFiscalCode(baseUserInfo.getBrokerFiscalCode())
        		.canManageUsers(baseUserInfo.getCanManageUsers())
        		.systemUser(baseUserInfo.getSystemUser())
        		.traceId(baseUserInfo.getTraceId())
        		.type(baseUserInfo.getType())
        		.build();
    }

    public LimitedScopeResource mapRequestToLimitedScope(LimitedTokenRequest request) {
        return LimitedScopeResource.builder()
        		.app(request.getApp())
        		.organization(null)
        		.resource(request.getResource())
        		.resourceId(request.getResourceId())
        		.singleUsage(request.getSingleUsage())
        		.build();
    }

    public IamUserInfoDTO mapBaseUserInfoToIamUserInfoDTO(BaseUserInfo baseUserInfo, String scope) {
        return IamUserInfoDTO.builder()
        		.traceId(baseUserInfo.getTraceId())
        		.userId(baseUserInfo.getUserId())
        		.fiscalCode(baseUserInfo.getFiscalCode())
        		.familyName(baseUserInfo.getFamilyName())
        		.name(baseUserInfo.getName())
        		.issuer(baseUserInfo.getIssuer())
        		.scope(scope)
        		.innerUserId(baseUserInfo.getUserId())
        		.mappedExternalUserId(baseUserInfo.getMappedExternalUserId())
        		.systemUser(Boolean.TRUE.equals(baseUserInfo.getSystemUser()))
        		.build();
    }

}
