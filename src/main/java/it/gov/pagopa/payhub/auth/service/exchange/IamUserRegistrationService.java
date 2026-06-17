package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;

@Service
public class IamUserRegistrationService {

    private final boolean organizationAccessMode;

    private final UserService userService;

    private final AccessTokenBuilderService accessTokenBuilderService;
    private final TokenStoreService tokenStoreService;

    public IamUserRegistrationService(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,
            UserService userService,
            AccessTokenBuilderService accessTokenBuilderService,
            TokenStoreService tokenStoreService
    ) {
        this.organizationAccessMode = organizationAccessMode;
        this.userService = userService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.tokenStoreService = tokenStoreService;
    }

    AccessToken registerUser(IamUserInfoDTO userInfo) {
        User user = userService.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer(), userInfo.getName(), userInfo.getFamilyName());

        MDC.put("externalUserId", user.getMappedExternalUserId());
        userInfo.setInnerUserId(user.getUserId());
        userInfo.setMappedExternalUserId(user.getMappedExternalUserId());
        AccessToken accessToken = accessTokenBuilderService.build(userInfo);
        tokenStoreService.save(accessToken.getAccessToken(), userInfo);

        if (organizationAccessMode) {
            if (CollectionUtils.isEmpty(userInfo.getOrganizationAccess().getRoles())) {
                throw new InvalidOrganizationAccessDataException(ErrorCodeConstants.ERROR_CODES_ROLES_NOT_FOUND, "No roles configured for organizationAccess " + userInfo.getOrganizationAccess());
            }

            userService.registerOperator(user, userInfo.getOrganizationAccess().getOrganizationIpaCode(),
                    new HashSet<>(userInfo.getOrganizationAccess().getRoles()), userInfo.getOrganizationAccess().getEmail(), accessToken.getAccessToken());
        }

        return accessToken;
    }
}
