package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.connector.organization.BrokerService;
import it.gov.pagopa.payhub.auth.connector.organization.OrgSubUnitService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.auth.utils.TestUtils;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class IamUserInfoDTO2UserInfoMapperTest {

    @Mock
    private OperatorsRepository operatorsRepositoryMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private BrokerService brokerServiceMock;
    @Mock
    private OrgSubUnitService orgSubUnitServiceMock;

    private IamUserInfoDTO2UserInfoMapper mapper;

    private final boolean organizationAccessMode = false;

    @BeforeEach
    void init() {
        mapper = new IamUserInfoDTO2UserInfoMapper(
                organizationAccessMode,
                operatorsRepositoryMock,
                organizationServiceMock,
                brokerServiceMock,
                orgSubUnitServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                operatorsRepositoryMock,
                organizationServiceMock,
                brokerServiceMock,
                orgSubUnitServiceMock);
    }

    @Test
    void givenCompleteDataWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId("EXTERNALUSERID")
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .email("EMAIL")
                        .build())
                .build();

        List<Operator> organizationRoles = List.of(Operator.builder()
                .operatorId("OPERATORID")
                .organizationIpaCode("ORG")
                .roles(Set.of("ROLE"))
                .email("EMAIL")
                .build());

        UserInfo expected = UserInfo.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess("ORG")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .operatorId("OPERATORID")
                        .organizationId(2L)
                        .organizationIpaCode("ORG")
                        .organizationFiscalCode("ORGFISCALCODE")
                        .roles(List.of("ROLE"))
                        .email("EMAIL")
                        .orgSubUnitCodes(List.of("SUB_UNIT_1", "SUB_UNIT_2"))
                        .build()))
                .brokerId(1L)
                .brokerFiscalCode("BROKERFISCALCODE")
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(organizationRoles);

        Organization mockOrganization = new Organization();
        mockOrganization.setOrganizationId(2L);
        mockOrganization.setOrgEmail("email@email.it");
        mockOrganization.setOrgFiscalCode("ORGFISCALCODE");
        mockOrganization.setBrokerId(1L);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Mockito.when(orgSubUnitServiceMock.getOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(2L, "MAPPEDEXTERNALUSERID", accessToken))
                .thenReturn(List.of(buildOrgSubUnit("SUB_UNIT_1"), buildOrgSubUnit("SUB_UNIT_2")));

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");

        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);

        TestUtils.checkNotNullFields(result);
        result.getOrganizations().forEach(TestUtils::checkNotNullFields);
    }

    @Test
    void givenNotOperatorsWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId("EXTERNALUSERID")
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .email("EMAIL")
                        .build())
                .build();

        UserInfo expected = UserInfo.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess("ORG")
                .organizations(Collections.emptyList())
                .brokerId(1L)
                .brokerFiscalCode("BROKERFISCALCODE")
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        Organization mockOrganization = new Organization();
        mockOrganization.setOrganizationId(2L);
        mockOrganization.setOrgEmail("email@email.it");
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");
        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);

        TestUtils.checkNotNullFields(result);
        result.getOrganizations().forEach(TestUtils::checkNotNullFields);
    }

    @Test
    void givenNoOrganizationAccessWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId("EXTERNALUSERID")
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .build();

        List<Operator> organizationRoles = List.of(Operator.builder()
                .operatorId("OPERATORID")
                .organizationIpaCode("ORG")
                .roles(Set.of("ROLE"))
                .email("EMAIL")
                .build());

        UserInfo expected = UserInfo.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .operatorId("OPERATORID")
                        .organizationId(2L)
                        .organizationIpaCode("ORG")
                        .organizationFiscalCode("ORGFISCALCODE")
                        .roles(List.of("ROLE"))
                        .email("EMAIL")
                        .orgSubUnitCodes(List.of("SUB_UNIT_1"))
                        .build()))
                .brokerId(1L)
                .brokerFiscalCode("BROKERFISCALCODE")
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(organizationRoles);

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        mockOrganization.setOrganizationId(2L);
        mockOrganization.setOrgFiscalCode("ORGFISCALCODE");
        mockOrganization.setOrgEmail("email@email.it");

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Mockito.when(orgSubUnitServiceMock.getOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(2L, "MAPPEDEXTERNALUSERID", accessToken))
                .thenReturn(List.of(buildOrgSubUnit("SUB_UNIT_1")));

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");

        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);

        TestUtils.checkNotNullFields(result, "organizationAccess");
        result.getOrganizations().forEach(TestUtils::checkNotNullFields);
    }

    @Test
    void givenSystemUserWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(Boolean.TRUE)
                .userId("EXTERNALUSERID")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .innerUserId(userId)
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("IPA_CODE")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("IPA_CODE")
                        .roles(Collections.singletonList(Constants.ROLE_ADMIN))
                        .build())
                .build();

        UserInfo expected = UserInfo.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(true)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("IPA_CODE")
                .organizationAccess("IPA_CODE")
                .organizations(Collections.singletonList(UserOrganizationRoles.builder()
                        .operatorId("INNERUSERID")
                        .organizationId(2L)
                        .organizationIpaCode("IPA_CODE")
                        .organizationFiscalCode("ORGFISCALCODE")
                        .email("email@email.it")
                        .roles(List.of(Constants.ROLE_ADMIN))
                        .orgSubUnitCodes(List.of("SUB_UNIT_1", "SUB_UNIT_2", "SUB_UNIT_3"))
                        .build()))
                .brokerId(1L)
                .brokerFiscalCode("BROKERFISCALCODE")
                .canManageUsers(false)
                .build();

        Organization mockOrganization = new Organization();
        mockOrganization.setOrganizationId(2L);
        mockOrganization.setOrgEmail("email@email.it");
        mockOrganization.setOrgFiscalCode("ORGFISCALCODE");
        mockOrganization.setBrokerId(1L);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("IPA_CODE"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Mockito.when(orgSubUnitServiceMock.getOrgSubUnitsByOrganizationId(2L, accessToken))
                .thenReturn(List.of(
                buildOrgSubUnit("SUB_UNIT_1"),
                buildOrgSubUnit("SUB_UNIT_2"),
                buildOrgSubUnit("SUB_UNIT_3")
        ));

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");

        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);

        TestUtils.checkNotNullFields(result);
        result.getOrganizations().forEach(TestUtils::checkNotNullFields);
    }


    @Test
    void givenLimitedScopeSystemUserWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        // Build LimitedScope resource with organization ipa code used to resolve broker
        UserOrganizationRoles org = UserOrganizationRoles.builder()
                .organizationIpaCode("ORG")
                .build();
        it.gov.pagopa.payhub.dto.generated.LimitedScopeResource resource = it.gov.pagopa.payhub.dto.generated.LimitedScopeResource.builder()
                .app("APP")
                .organization(org)
                .resource("RES")
                .resourceId("RID")
                .singleUsage(false)
                .build();

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type(it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope.class.getSimpleName())
                .traceId("traceId")
                .systemUser(true)
                .userId("EXTERNALUSERID")
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .organizationAccess(IamUserOrganizationRolesDTO.builder().organizationIpaCode("ORG").build())
                .resource(resource)
                .build();

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");
        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertInstanceOf(UserInfoLimitedScope.class, result);
        it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope limited = (it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope) result;
        Assertions.assertEquals(it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope.class.getSimpleName(), limited.getType());
        Assertions.assertEquals("traceId", limited.getTraceId());
        Assertions.assertEquals(Boolean.TRUE, limited.getSystemUser());
        Assertions.assertEquals(userId, limited.getUserId());
        Assertions.assertEquals("MAPPEDEXTERNALUSERID", limited.getMappedExternalUserId());
        Assertions.assertEquals("FISCALCODE", limited.getFiscalCode());
        Assertions.assertEquals("FAMILYNAME", limited.getFamilyName());
        Assertions.assertEquals(resource, limited.getResource());
        Assertions.assertEquals(false, limited.getCanManageUsers());
        Assertions.assertEquals(1L, limited.getBrokerId());
        Assertions.assertEquals("BROKERFISCALCODE", limited.getBrokerFiscalCode());
    }

    @Test
    void givenLimitedScopeUserWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        // operatorsRepository is invoked before branching in user path
        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        UserOrganizationRoles org = UserOrganizationRoles.builder()
                .organizationIpaCode("ORG")
                .build();
        it.gov.pagopa.payhub.dto.generated.LimitedScopeResource resource = it.gov.pagopa.payhub.dto.generated.LimitedScopeResource.builder()
                .app("APP")
                .organization(org)
                .resource("RES")
                .resourceId("RID")
                .singleUsage(true)
                .build();

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type(it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope.class.getSimpleName())
                .traceId("traceId")
                .systemUser(false)
                .userId("EXTERNALUSERID")
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .resource(resource)
                .build();

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        mockBroker.setBrokerFiscalCode("BROKERFISCALCODE");
        Mockito.when(brokerServiceMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertInstanceOf(UserInfoLimitedScope.class, result);
        it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope limited = (it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope) result;
        Assertions.assertEquals(it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope.class.getSimpleName(), limited.getType());
        Assertions.assertEquals("traceId", limited.getTraceId());
        Assertions.assertNotEquals(Boolean.TRUE, limited.getSystemUser());
        Assertions.assertEquals(userId, limited.getUserId());
        Assertions.assertEquals("MAPPEDEXTERNALUSERID", limited.getMappedExternalUserId());
        Assertions.assertEquals("FISCALCODE", limited.getFiscalCode());
        Assertions.assertEquals("FAMILYNAME", limited.getFamilyName());
        Assertions.assertEquals(resource, limited.getResource());
        // organizationAccessMode is false in this test, so canManageUsers should be true for non-system users
        Assertions.assertEquals(true, limited.getCanManageUsers());
        Assertions.assertEquals(1L, limited.getBrokerId());
        Assertions.assertEquals("BROKERFISCALCODE", limited.getBrokerFiscalCode());
    }

    @Test
    void givenNoAssociatedOrgSubUnitsWhenApplyThenEmptyCodes() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .type("UserInfo")
                .traceId("traceId")
                .systemUser(false)
                .innerUserId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .build();

        Operator operator = Operator.builder()
                .operatorId("OPERATORID")
                .organizationIpaCode("ORG")
                .roles(Set.of("ROLE"))
                .email("EMAIL")
                .build();

        Organization organization = new Organization();
        organization.setOrganizationId(2L);
        organization.setOrgFiscalCode("ORGFISCALCODE");

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId))
                .thenReturn(List.of(operator));

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("ORG", accessToken))
                .thenReturn(organization);

        Mockito.when(orgSubUnitServiceMock.getOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(2L, "MAPPEDEXTERNALUSERID", accessToken))
                .thenReturn(Collections.emptyList());

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(Collections.emptyList(), result.getOrganizations().getFirst().getOrgSubUnitCodes());
    }

    private OrgSubUnit buildOrgSubUnit(String subUnitCode) {
        OrgSubUnit orgSubUnit = new OrgSubUnit();
        orgSubUnit.setSubUnitCode(subUnitCode);
        return orgSubUnit;
    }
}
