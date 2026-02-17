package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.auth.mapper.Client2UserInfoMapper;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static it.gov.pagopa.payhub.auth.service.m2m.AuthorizeClientCredentialsRequestService.PIATTAFORMA_UNITARIA_CLIENT_ID_PREFIX;

public final class SecurityUtils {
    private SecurityUtils(){}

    public static final String SYSTEM_USERID_PREFIX  = Client2UserInfoMapper.buildSystemMappedExternalUserId(PIATTAFORMA_UNITARIA_CLIENT_ID_PREFIX);
    public static final String HEADER_USER_ID = "X-user-id";

    /** It will return user's session accessToken */
    public static String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) {
            return (String) authentication.getCredentials();
        } else {
            return null;
        }
    }

    /** It will return user's session data from ThreadLocal */
    public static UserInfo getPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) {
            return (UserInfo) authentication.getPrincipal();
        } else {
            return null;
        }
    }

    /** It will return user's session roles on requested organization IPA code retrieving it from ThreadLocal */
    public static List<String> getPrincipalRoles(String organizationIpaCode){
        return Objects.requireNonNull(getPrincipal()).getOrganizations().stream()
                .filter(o -> organizationIpaCode.equals(o.getOrganizationIpaCode()))
                .findFirst()
                .map(UserOrganizationRoles::getRoles)
                .orElse(Collections.emptyList());
    }

    public static String getCurrentUserExternalId(){
        UserInfo loggedUser = getPrincipal();
        if(loggedUser!=null) {
            return resolvePuSystemUser(loggedUser.getMappedExternalUserId());
        } else {
            return null;
        }
    }

    public static String resolvePuSystemUser(String mappedExternalUserId) {
        if(mappedExternalUserId != null && mappedExternalUserId.startsWith(SYSTEM_USERID_PREFIX) && RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes){
            HttpServletRequest requestAttributes = servletRequestAttributes.getRequest();
            mappedExternalUserId = ObjectUtils.firstNonNull(requestAttributes.getHeader(HEADER_USER_ID), mappedExternalUserId);
        }
        return mappedExternalUserId;
    }

    /** It will return true if the user's session has ROLE_ADMIN role on requested organization IPA code retrieving it from ThreadLocal */
    public static boolean isPrincipalAdmin(String organizationIpaCode){
        return getPrincipalRoles(organizationIpaCode).contains(Constants.ROLE_ADMIN);
    }

    /** It will return true if the user has at least a ROLE_ADMIN on one of organization retrieving it from ThreadLocal */
    public static boolean hasAdminRole() {
        UserInfo principal = getPrincipal();
        if(principal!=null) {
            return principal.getOrganizations().stream()
                    .flatMap(o -> o.getRoles().stream())
                    .anyMatch(Constants.ROLE_ADMIN::equals);
        } else {
            return false;
        }
    }

    public static String removePiiFromURI(URI uri){
        return uri != null
                ? uri.toString().replaceAll("=[^&]*", "=***")
                : null;
    }
}
