package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;

@Service
public class IamUserRegistrationService {

    private final boolean organizationAccessMode;

    private final UserService userService;

    public IamUserRegistrationService(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,

            UserService userService
    ) {
        this.organizationAccessMode = organizationAccessMode;
        this.userService = userService;
    }

    User registerUser(IamUserInfoDTO userInfo) {
        User user = userService.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());

        if (organizationAccessMode) {
            if (CollectionUtils.isEmpty(userInfo.getOrganizationAccess().getRoles())) {
                throw new InvalidOrganizationAccessDataException("No roles configured for organizationAccess " + userInfo.getOrganizationAccess());
            }

            userService.registerOperator(user.getUserId(), userInfo.getOrganizationAccess().getOrganizationIpaCode(), new HashSet<>(userInfo.getOrganizationAccess().getRoles()));
        }

        return user;
    }
}
