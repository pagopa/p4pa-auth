package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class SecurityUtilsTest {

    @AfterEach
    void clear() {
        clearSecurityContext();
    }

    @Test
    void testGetPrincipal() {
        // Given
        UserInfo expectedUserInfo = new UserInfo();
        configureSecurityContext(expectedUserInfo, "TOKEN");

        // When
        UserInfo result = SecurityUtils.getPrincipal();

        // Then
        Assertions.assertSame(expectedUserInfo, result);
    }

    @Test
    void testGetAccessToken() {
        // Given
        String expectedAccessToken = "TOKEN";
        configureSecurityContext(null, expectedAccessToken);

        // When
        String result = SecurityUtils.getAccessToken();

        // Then
        Assertions.assertSame(expectedAccessToken, result);
    }

    @Test
    void testGetPrincipalRoles() {
        // Given
        List<String> expectedRoles = List.of("ROLE");
        UserInfo expectedUserInfo = UserInfo.builder()
                .organizations(List.of(
                        UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG")
                                .roles(expectedRoles)
                                .build(),
                        UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG2")
                                .roles(List.of("ROLE2"))
                                .build())
                )
                .build();
        configureSecurityContext(expectedUserInfo, "TOKEN");

        // When
        List<String> result1 = SecurityUtils.getPrincipalRoles("ORG");
        List<String> result2 = SecurityUtils.getPrincipalRoles("ORG3");

        // Then
        Assertions.assertSame(expectedRoles, result1);
        Assertions.assertSame(Collections.emptyList(), result2);
    }

    @Test
    void testIsPrincipalAdmin() {
        // Given
        UserInfo expectedUserInfo = UserInfo.builder()
                .organizations(List.of(
                        UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG")
                                .roles(List.of("ROLE_ADMIN"))
                                .build(),
                        UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG2")
                                .roles(List.of("ROLE2"))
                                .build())
                )
                .build();
        configureSecurityContext(expectedUserInfo, "TOKEN");

        // When
        boolean result1 = SecurityUtils.isPrincipalAdmin("ORG");
        boolean result2 = SecurityUtils.isPrincipalAdmin("ORG2");

        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    @Test
    void testHasAdminRole() {
        // Given
        UserInfo expectedUserInfo = UserInfo.builder()
            .organizations(List.of(
                UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG")
                    .roles(List.of("ROLE_ADMIN"))
                    .build(),
                UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of("ROLE2"))
                    .build())
            )
            .build();
        configureSecurityContext(expectedUserInfo, "TOKEN");

        // When
        boolean result = SecurityUtils.hasAdminRole();
        Assertions.assertTrue(result);
    }

    public static void configureSecurityContext(UserInfo expectedUserInfo, String token) {
        SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(expectedUserInfo, token)));
    }

    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenUriWhenRemovePiiFromURIThenOk(){
        String result = SecurityUtils.removePiiFromURI(URI.create("https://host/path?param1=PII&param2=noPII"));
        Assertions.assertEquals("https://host/path?param1=***&param2=***", result);
    }

    @Test
    void givenNullUriWhenRemovePiiFromURIThenOk(){
        Assertions.assertNull(SecurityUtils.removePiiFromURI(null));
    }

    //region test getCurrentUserExternalId
    @Test
    void givenJwtWhenGetCurrentUserExternalIdThenReturnPrincipalName(){
        // Given
        String principalName = "PRINCIPALNAME";
        UserInfo loggedUser = new UserInfo();
        loggedUser.setMappedExternalUserId(principalName);
        configureSecurityContext(loggedUser, "token");

        // When
        String result = SecurityUtils.getCurrentUserExternalId();

        // Then
        Assertions.assertSame(principalName, result);
    }

    @Test
    void givenPuSystemUserAndUserIdProvidedWhenGetCurrentUserExternalIdThenReturnUserId(){
        // Given
        String expectedUserId = "USERID";
        String principalName = SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE";
        UserInfo loggedUser = new UserInfo();
        loggedUser.setMappedExternalUserId(principalName);
        configureSecurityContext(loggedUser, "token");
        configureXUserIdHeader(expectedUserId);

        // When
        String result = SecurityUtils.getCurrentUserExternalId();

        // Then
        Assertions.assertSame(expectedUserId, result);
    }

    public static void configureXUserIdHeader(String expectedUserId) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SecurityUtils.HEADER_USER_ID, expectedUserId);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void givenPuSystemUserAndNotUserIdProvidedWhenGetCurrentUserExternalIdThenReturnUserId(){
        // Given
        String principalName = SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE";
        UserInfo loggedUser = new UserInfo();
        loggedUser.setMappedExternalUserId(principalName);
        configureSecurityContext(loggedUser, "token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // When
        String result = SecurityUtils.getCurrentUserExternalId();

        // Then
        Assertions.assertSame(principalName, result);
    }

    @Test
    void givenPuSystemUserAndNotHttpContextWhenGetCurrentUserExternalIdThenReturnUserId(){
        // Given
        String principalName = SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE";
        UserInfo loggedUser = new UserInfo();
        loggedUser.setMappedExternalUserId(principalName);
        configureSecurityContext(loggedUser, "token");

        // When
        String result = SecurityUtils.getCurrentUserExternalId();

        // Then
        Assertions.assertSame(principalName, result);
    }
//endregion
}
