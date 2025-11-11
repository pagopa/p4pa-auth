package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.dto.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LimitedScopeTokenMapperTest {

  private LimitedScopeTokenMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new LimitedScopeTokenMapper();
  }

  @Test
  void givenLimitedTokenRequestAndOrganizationWhenMapRequestToLimitedScopeResourceThenAllFieldsAreMapped() {
    // Given
    String app = "my-app";
    String resource = "PAYMENT";
    String resourceId = "res-123";
    Boolean singleUsage = Boolean.TRUE;
    long organizationId = 123L;

    LimitedTokenRequest request = LimitedTokenRequest.builder()
      .app(app)
      .resource(resource)
      .resourceId(resourceId)
      .singleUsage(singleUsage)
      .organizationId(organizationId)
      .build();

    UserOrganizationRoles organization = UserOrganizationRoles.builder()
      .organizationId(organizationId)
      .build();

    LimitedScopeResource expected = LimitedScopeResource.builder()
      .app(app)
      .organization(organization)
      .resource(resource)
      .resourceId(resourceId)
      .singleUsage(singleUsage)
      .build();

    // When
    LimitedScopeResource actual = mapper.mapRequestToLimitedScopeResource(request, organization);

    // Then
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void givenBaseUserInfoWithMatchingOrganizationAndRequestWhenMapToIamUserInfoDTOThenOrganizationKeptAndSystemUserTrue() {
    // Given
    long organizationId = 456L;

    LimitedTokenRequest request = LimitedTokenRequest.builder()
      .app("app-x")
      .resource("REPORT")
      .resourceId("rep-999")
      .singleUsage(Boolean.FALSE)
      .organizationId(organizationId)
      .build();

    UserOrganizationRoles matchingOrg = UserOrganizationRoles.builder()
      .organizationId(organizationId)
      .build();

    List<UserOrganizationRoles> orgs = Arrays.asList(
      UserOrganizationRoles.builder().organizationId(111L).build(),
      matchingOrg
    );

    UserInfo userInfo = UserInfo.builder()
      .traceId("trace-1")
      .userId("user-1")
      .fiscalCode("RSSMRA80A01H501U")
      .familyName("Rossi")
      .name("Mario")
      .issuer("IDP")
      .mappedExternalUserId("ext-1")
      .systemUser(Boolean.TRUE)
      .organizations(orgs)
      .build();

    LimitedScopeResource expectedResource = LimitedScopeResource.builder()
      .app(request.getApp())
      .organization(matchingOrg)
      .resource(request.getResource())
      .resourceId(request.getResourceId())
      .singleUsage(request.getSingleUsage())
      .build();

    IamUserInfoDTO expected = IamUserInfoDTO.builder()
      .type(UserInfoLimitedScope.class.getSimpleName())
      .traceId(userInfo.getTraceId())
      .userId(userInfo.getUserId())
      .fiscalCode(userInfo.getFiscalCode())
      .familyName(userInfo.getFamilyName())
      .name(userInfo.getName())
      .issuer(userInfo.getIssuer())
      .resource(expectedResource)
      .innerUserId(userInfo.getUserId())
      .mappedExternalUserId(userInfo.getMappedExternalUserId())
      .systemUser(true)
      .build();

    // When
    IamUserInfoDTO actual = mapper.mapBaseUserInfoToIamUserInfoDTO(userInfo, request);

    // Then
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void givenBaseUserInfoWithoutMatchingOrganizationAndRequestWhenMapToIamUserInfoDTOThenOrganizationNullAndSystemUserFalse() {
    // Given
    long requestOrgId = 999L; // not present in user's organizations

    LimitedTokenRequest request = LimitedTokenRequest.builder()
      .app("another-app")
      .resource("INVOICE")
      .resourceId("inv-321")
      .singleUsage(Boolean.TRUE)
      .organizationId(requestOrgId)
      .build();

    List<UserOrganizationRoles> orgs = Arrays.asList(
      UserOrganizationRoles.builder().organizationId(1L).build(),
      UserOrganizationRoles.builder().organizationId(2L).build()
    );

    // systemUser is null -> should become false
    UserInfo userInfo = UserInfo.builder()
      .traceId("trace-2")
      .userId("user-2")
      .fiscalCode("BNCLGU80A01H501U")
      .familyName("Bianchi")
      .name("Luca")
      .issuer("SPID")
      .mappedExternalUserId("ext-2")
      .systemUser(null)
      .organizations(orgs)
      .build();

    LimitedScopeResource expectedResource = LimitedScopeResource.builder()
      .app(request.getApp())
      .organization(null) // no matching org
      .resource(request.getResource())
      .resourceId(request.getResourceId())
      .singleUsage(request.getSingleUsage())
      .build();

    IamUserInfoDTO expected = IamUserInfoDTO.builder()
      .type(UserInfoLimitedScope.class.getSimpleName())
      .traceId(userInfo.getTraceId())
      .userId(userInfo.getUserId())
      .fiscalCode(userInfo.getFiscalCode())
      .familyName(userInfo.getFamilyName())
      .name(userInfo.getName())
      .issuer(userInfo.getIssuer())
      .resource(expectedResource)
      .innerUserId(userInfo.getUserId())
      .mappedExternalUserId(userInfo.getMappedExternalUserId())
      .systemUser(false)
      .build();

    // When
    IamUserInfoDTO actual = mapper.mapBaseUserInfoToIamUserInfoDTO(userInfo, request);

    // Then
    Assertions.assertEquals(expected, actual);
  }
}
