package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidScopedAccessTokenRequest;
import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.LimitedScopeTokenMapper;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitedTokenServiceImpl implements LimitedTokenService {

    private final LimitedScopeTokenMapper limitedScopeTokenMapper;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final TokenStoreService tokenStoreService;

    @Override
    public AccessToken generate(LimitedTokenRequest request) {
        UserInfo userInfo = SecurityUtils.getPrincipal();

        if (userInfo instanceof UserInfoLimitedScope) {
            throw new InvalidScopedAccessTokenRequest("Session token is already scoped");
        }

        validateOrganization(userInfo, request.getOrganizationId());

        IamUserInfoDTO iamUser = limitedScopeTokenMapper.mapBaseUserInfoToIamUserInfoDTO(userInfo, request);
        AccessToken accessToken = accessTokenBuilderService.build(
                iamUser,
                request.getExpireInSeconds() != null ? request.getExpireInSeconds().intValue() : null
        );
        MDC.put("externalUserId", iamUser.getMappedExternalUserId());
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);

        return accessToken;
    }

    private void validateOrganization(UserInfo userInfo, Long organizationId) {
        userInfo.getOrganizations().stream()
            .filter(org -> org.getOrganizationId().equals(organizationId))
            .findFirst().orElseThrow(() -> new UserUnauthorizedException("User not allowed on organization"));
    }
}
