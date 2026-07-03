package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidScopedAccessTokenRequest;
import it.gov.pagopa.payhub.auth.exception.custom.UserUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.LimitedScopeTokenMapper;
import it.gov.pagopa.payhub.auth.utils.SecurityUtils;
import it.gov.pagopa.payhub.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LimitedTokenServiceImplTest {

  @Mock
  private LimitedScopeTokenMapper limitedScopeTokenMapper;
  @Mock
  private AccessTokenBuilderService accessTokenBuilderService;
  @Mock
  private TokenStoreService tokenStoreService;

  private LimitedTokenServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new LimitedTokenServiceImpl(limitedScopeTokenMapper, accessTokenBuilderService, tokenStoreService);
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void givenLimitedScopePrincipalWhenGenerateThenThrowInvalidScopedAccessTokenRequest() {
    // Given
    LimitedTokenRequest request = LimitedTokenRequest.builder()
        .app("app")
        .resource("RES")
        .resourceId("RES-ID")
        .singleUsage(Boolean.TRUE)
        .organizationId(10L)
        .build();

    UserInfoLimitedScope limitedUser = UserInfoLimitedScope.builder().userId("u1").build();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getPrincipal).thenReturn(limitedUser);

      // When
      // Then
      assertThrows(InvalidScopedAccessTokenRequest.class, () -> service.generate(request));

      // Then
      verifyNoInteractions(limitedScopeTokenMapper, accessTokenBuilderService, tokenStoreService);
    }
  }

  @Test
  void givenBaseUserInfoPrincipalWhenGenerateThenBuildSaveReturnTokenAndPutExternalUserIdInMDC() {
    // Given
    LimitedTokenRequest request = LimitedTokenRequest.builder()
        .app("app-x")
        .resource("PAYMENT")
        .resourceId("P-123")
        .singleUsage(Boolean.FALSE)
        .organizationId(22L)
        .expireInSeconds(3600L)
        .build();

    UserInfo baseUser = UserInfo.builder()
        .userId("user-123")
        .organizations(List.of(UserOrganizationRoles.builder().organizationId(22L).build()))
        .traceId("trace-abc")
        .build();

    IamUserInfoDTO iamUser = IamUserInfoDTO.builder()
        .userId(baseUser.getUserId())
        .mappedExternalUserId("ext-999")
        .build();

    AccessToken expectedToken = AccessToken.builder()
        .accessToken("HEADER.x.y")
        .build();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getPrincipal).thenReturn(baseUser);

      when(limitedScopeTokenMapper.mapBaseUserInfoToIamUserInfoDTO(baseUser, request)).thenReturn(iamUser);
      when(accessTokenBuilderService.build(iamUser, 3600, false)).thenReturn(expectedToken);

      // When
      AccessToken result = service.generate(request);

      // Then
      assertEquals(expectedToken, result);
      // Then
      // verify saving to store with the token string and user info
      verify(tokenStoreService, times(1)).save(expectedToken.getAccessToken(), iamUser);

      // Then
      // Verify that MDC has been populated with external user id
      assertEquals("ext-999", MDC.get("externalUserId"));

      // Then
      verify(limitedScopeTokenMapper, times(1)).mapBaseUserInfoToIamUserInfoDTO(baseUser, request);
      verify(accessTokenBuilderService, times(1)).build(iamUser, 3600, false);
      verifyNoMoreInteractions(limitedScopeTokenMapper, accessTokenBuilderService, tokenStoreService);
    }
  }

    @Test
    void givenInvalidOrganizationWhenGenerateThenThrowUserNotAuthorizedException() {
        // Given
        LimitedTokenRequest request = LimitedTokenRequest.builder()
                .app("app")
                .resource("RES")
                .resourceId("RES-ID")
                .singleUsage(Boolean.TRUE)
                .organizationId(10L)
                .build();

        UserInfo userInfo = UserInfo.builder()
                .userId("u1")
                .organizations(List.of(UserOrganizationRoles.builder().organizationId(5L).build()))
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getPrincipal).thenReturn(userInfo);

            // When
            // Then
            assertThrows(UserUnauthorizedException.class, () -> service.generate(request));

            // Then
            verifyNoInteractions(limitedScopeTokenMapper, accessTokenBuilderService, tokenStoreService);
        }
    }
}
