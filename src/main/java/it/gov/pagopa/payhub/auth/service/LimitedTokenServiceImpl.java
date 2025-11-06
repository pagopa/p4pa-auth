package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidScopedAccessTokenRequest;
import it.gov.pagopa.payhub.auth.mapper.LimitedScopeTokenMapper;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.dto.generated.*;
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
    public AccessToken build(LimitedTokenRequest request) {
        UserInfo userInfo = SecurityUtils.getPrincipal();

        if (userInfo instanceof UserInfoLimitedScope) {
            throw new InvalidScopedAccessTokenRequest("Session token is already scoped");
        }

        BaseUserInfo baseUserInfo = (BaseUserInfo)  userInfo;
        IamUserInfoDTO iamUser = limitedScopeTokenMapper.mapBaseUserInfoToIamUserInfoDTO(baseUserInfo, request.getResource());
        AccessToken accessToken = accessTokenBuilderService.build(iamUser);
        MDC.put("externalUserId", iamUser.getMappedExternalUserId());
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);

        return accessToken;
    }
}
