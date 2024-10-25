package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class IamUserInfoDTO2UserInfoMapperTest {

    @Mock
    private UsersRepository usersRepositoryMock;
    @Mock
    private OperatorsRepository operatorsRepositoryMock;

    private IamUserInfoDTO2UserInfoMapper mapper;


    @BeforeEach
    void init() {
        mapper = new IamUserInfoDTO2UserInfoMapper(usersRepositoryMock, operatorsRepositoryMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(usersRepositoryMock, operatorsRepositoryMock);
    }

    @Test
    void givenNotUserWhenApplyThenUserNotFoundException(){
        // Given
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .innerUserId("INNERUSERID")
                .build();

        Mockito.when(usersRepositoryMock.findById(iamUserInfo.getInnerUserId())).thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThrows(UserNotFoundException.class, () -> mapper.apply(iamUserInfo));
    }

    @Test
    void givenCompleteDataWhenApplyThenOk(){
        // Given
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .innerUserId("INNERUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .email("EMAIL")
                        .build())
                .build();

        User user = User.builder()
                .userId(iamUserInfo.getInnerUserId())
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .build();

        List<Operator> organizationRoles = List.of(Operator.builder()
                        .operatorId("OPERATORID")
                        .organizationIpaCode("ORG")
                        .roles(Set.of("ROLE"))
                .build());

        UserInfo expected = UserInfo.builder()
          .fiscalCode("FISCALCODE")
          .familyName("FAMILYNAME")
          .name("NAME")
          .issuer("ISSUER")
          .organizationAccess("ORG")
          .build();

        testApplyOk(iamUserInfo, user, organizationRoles, expected);
    }

    @Test
    void givenNotOperatorsWhenApplyThenOk(){
        // Given
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .innerUserId("INNERUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .email("EMAIL")
                        .build())
                .build();

        User user = User.builder()
                .userId(iamUserInfo.getInnerUserId())
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .build();

        UserInfo expected = UserInfo.builder()
          .fiscalCode("FISCALCODE")
          .familyName("FAMILYNAME")
          .name("NAME")
          .issuer("ISSUER")
          .organizationAccess("ORG")
          .build();

        testApplyOk(iamUserInfo, user, Collections.emptyList(), expected);
    }

    @Test
    void givenNoOrganizationAccessWhenApplyThenOk(){
        // Given
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .innerUserId("INNERUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .build();

        User user = User.builder()
                .userId(iamUserInfo.getInnerUserId())
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .build();

        List<Operator> organizationRoles = List.of(Operator.builder()
                .operatorId("OPERATORID")
                .organizationIpaCode("ORG")
                .roles(Set.of("ROLE"))
                .email("EMAIL")
                .build());
        
        UserInfo expected = UserInfo.builder()
          .fiscalCode("FISCALCODE")
          .familyName("FAMILYNAME")
          .name("NAME")
          .issuer("ISSUER")
          .build();

        testApplyOk(iamUserInfo, user, organizationRoles, expected);
    }

    @Test
    void givenSystemUserWhenApplyThenOk(){
        // Given
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
          .systemUser(Boolean.TRUE)
          .userId("EXTERNALUSERID")
          .innerUserId("INNERUSERID")
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
          .userId("EXTERNALUSERID")
          .mappedExternalUserId("IPA_CODE-WS_USER")
          .fiscalCode("FISCALCODE")
          .familyName("FAMILYNAME")
          .name("NAME")
          .issuer("IPA_CODE")
          .organizations(Collections.singletonList(UserOrganizationRoles.builder()
            .organizationIpaCode("IPA_CODE")
            .roles(Collections.singletonList(Constants.ROLE_ADMIN))
            .build()))
          .build();

        testApplyOk(iamUserInfo, null, null, expected);
    }

    private void testApplyOk(IamUserInfoDTO iamUserInfo, User user, List<Operator> organizationRoles, UserInfo expected) {

        if (!iamUserInfo.isSystemUser()) {
            Mockito.when(usersRepositoryMock.findById(iamUserInfo.getInnerUserId())).thenReturn(Optional.of(user));
            Mockito.when(operatorsRepositoryMock.findAllByUserId(user.getUserId())).thenReturn(organizationRoles);
            expected.setUserId(user.getUserId());
            expected.setMappedExternalUserId(user.getMappedExternalUserId());
            expected.setOrganizations(organizationRoles.stream()
              .map(r -> UserOrganizationRoles.builder()
                .operatorId(r.getOperatorId())
                .organizationIpaCode(r.getOrganizationIpaCode())
                .roles(new ArrayList<>(r.getRoles()))
                .email(r.getEmail())
                .build())
              .toList());
        }
        // When
        UserInfo result = mapper.apply(iamUserInfo);

        // Then
        Assertions.assertEquals(expected, result);
    }
}
