package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.connector.client.OrganizationSearchClient;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
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
    private OrganizationSearchClient organizationSearchClientMock;

    private IamUserInfoDTO2UserInfoMapper mapper;

    private final boolean organizationAccessMode = false;

    @BeforeEach
    void init() {
        mapper = new IamUserInfoDTO2UserInfoMapper(organizationAccessMode, operatorsRepositoryMock, organizationSearchClientMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(operatorsRepositoryMock, organizationSearchClientMock);
    }

    @Test
    void givenCompleteDataWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
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
                        .organizationIpaCode("ORG")
                        .roles(List.of("ROLE"))
                        .email("EMAIL")
                        .build()))
                .brokerId(1L)
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(organizationRoles);

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void givenNotOperatorsWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
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
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void givenNoOrganizationAccessWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
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
                .systemUser(false)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .operatorId("OPERATORID")
                        .organizationIpaCode("ORG")
                        .roles(List.of("ROLE"))
                        .email("EMAIL")
                        .build()))
                .brokerId(1L)
                .canManageUsers(!organizationAccessMode)
                .build();

        Mockito.when(operatorsRepositoryMock.findAllByUserId(userId)).thenReturn(organizationRoles);

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getOrganizationByIpaCode(Mockito.eq("ORG"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void givenSystemUserWhenApplyThenOk() {
        String accessToken = "sampleAccessToken";
        String userId = "INNERUSERID";

        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
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
                .systemUser(true)
                .userId(userId)
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("IPA_CODE")
                .organizations(Collections.singletonList(UserOrganizationRoles.builder()
                        .organizationIpaCode("IPA_CODE")
                        .roles(List.of(Constants.ROLE_ADMIN))
                        .build()))
                .brokerId(1L)
                .build();

        Organization mockOrganization = new Organization();
        mockOrganization.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getOrganizationByIpaCode(Mockito.eq("IPA_CODE"), Mockito.anyString()))
                .thenReturn(mockOrganization);

        Broker mockBroker = new Broker();
        mockBroker.setBrokerId(1L);
        Mockito.when(organizationSearchClientMock.getBrokerById(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(mockBroker);

        UserInfo result = mapper.apply(iamUserInfo, accessToken);

        Assertions.assertEquals(expected, result);
    }


}
