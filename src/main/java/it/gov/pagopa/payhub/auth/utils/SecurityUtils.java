package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.dto.generated.BaseUserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public final class SecurityUtils {
    private SecurityUtils(){}

    /** It will return user's session accessToken */
    public static String getAccessToken() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }

    /** It will return user's session data from ThreadLocal */
    public static UserInfo getPrincipal(){
        return (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /** It will return user's session roles on requested organization IPA code retrieving it from ThreadLocal */
    public static List<String> getPrincipalRoles(String organizationIpaCode){
        return getOrganizations(getPrincipal()).stream()
                .filter(o -> organizationIpaCode.equals(o.getOrganizationIpaCode()))
                .findFirst()
                .map(UserOrganizationRoles::getRoles)
                .orElse(Collections.emptyList());
    }

    /** It will return true if the user's session has ROLE_ADMIN role on requested organization IPA code retrieving it from ThreadLocal */
    public static boolean isPrincipalAdmin(String organizationIpaCode){
        return getPrincipalRoles(organizationIpaCode).contains(Constants.ROLE_ADMIN);
    }

    /** It will return true if the user has at least a ROLE_ADMIN on one of organization retrieving it from ThreadLocal */
    public static boolean hasAdminRole() {
        return getOrganizations(getPrincipal()).stream()
            .flatMap(o -> o.getRoles().stream())
            .anyMatch(Constants.ROLE_ADMIN::equals);
    }

    public static String removePiiFromURI(URI uri){
        return uri != null
                ? uri.toString().replaceAll("=[^&]*", "=***")
                : null;
    }

    public static List<UserOrganizationRoles> getOrganizations(UserInfo userInfo){
        return switch (userInfo) {
            case BaseUserInfo base -> base.getOrganizations();
            case UserInfoLimitedScope limited -> limited.getOrganizations();
            default -> List.of();
        };
    }

    public static String getMappedExternalUserId(UserInfo userInfo){
        return switch (userInfo) {
            case BaseUserInfo base -> base.getMappedExternalUserId();
            case UserInfoLimitedScope limited -> limited.getMappedExternalUserId();
            default -> null;
        };
    }

    public static Long getBrokerId(UserInfo userInfo){
        return switch (userInfo) {
            case BaseUserInfo base -> base.getBrokerId();
            case UserInfoLimitedScope limited -> limited.getBrokerId();
            default -> null;
        };
    }
}
