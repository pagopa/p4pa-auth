package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

public final class SecurityUtils {
    private SecurityUtils(){}

    /** It will return user's session data from ThreadLocal */
    public static UserInfo getPrincipal(){
        return (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** It will return user's session roles on requested organization IPA code retrieving it from ThreadLocal */
    public static List<String> getPrincipalRoles(String organizationIpaCode){
        return getPrincipal().getOrganizations().stream()
                .filter(o -> organizationIpaCode.equals(o.getOrganizationIpaCode()))
                .findFirst()
                .map(UserOrganizationRoles::getRoles)
                .orElse(Collections.emptyList());
    }

    /** It will return true if the user's session has ROLE_ADMIN role on requested organization IPA code retrieving it from ThreadLocal */
    public static boolean isPrincipalAdmin(String organizationIpaCode){
        return getPrincipalRoles(organizationIpaCode).contains(Constants.ROLE_ADMIN);
    }
}
