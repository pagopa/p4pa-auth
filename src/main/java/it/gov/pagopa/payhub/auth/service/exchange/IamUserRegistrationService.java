package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    void registerUser(UserInfo userInfo){
        User user = userService.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());

        if(organizationAccessMode){
            Optional<Set<String>> roles = userInfo.getOrganizations().stream()
                    .filter(r -> userInfo.getOrganizationAccess().equals(r.getIpaCode()))
                    .findFirst()
                    .map(userOrganizationRoles -> new HashSet<>(userOrganizationRoles.getRoles()));

            if(roles.isEmpty()){
                throw new InvalidOrganizationAccessDataException("No roles configured for organizationAccess " + userInfo.getOrganizationAccess() + "; organizations: " + userInfo.getOrganizations());
            }

            userService.registerOperator(user.getUserId(), userInfo.getOrganizationAccess(), roles.get());
        }
    }
}
