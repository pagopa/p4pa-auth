package it.gov.pagopa.payhub.auth.utils;

import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Collections;
import java.util.List;

class SecurityUtilsTest {

    @Test
    void testGetPrincipal() {
        // Given
        UserInfo expectedUserInfo = new UserInfo();
        configureSecurityContext(expectedUserInfo);

        // When
        UserInfo result = SecurityUtils.getPrincipal();

        // Then
        Assertions.assertSame(expectedUserInfo, result);
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
        configureSecurityContext(expectedUserInfo);

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
        configureSecurityContext(expectedUserInfo);

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
        configureSecurityContext(expectedUserInfo);

        // When
        boolean result = SecurityUtils.hasAdminRole();
        Assertions.assertTrue(result);
    }

    private static void configureSecurityContext(UserInfo expectedUserInfo) {
        SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(expectedUserInfo, null)));
    }
}
