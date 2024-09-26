package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Operator2UserInfoMapperTest {

  private final Operator2UserInfoMapper mapper = new Operator2UserInfoMapper();

  @Test
  void test() {
    // Given
    User user = User.builder()
        .userId("USERID")
        .mappedExternalUserId("MAPPEDEXTERNALUSERID")
        .userCode("USERCODE")
        .fiscalCode("FISCALCODE")
        .firstName("FIRSTNAME")
        .lastName("LASTNAME")
        .build();

    List<Operator> organizationRoles = List.of(Operator.builder()
        .operatorId("OPERATORID")
        .organizationIpaCode("ORG")
        .roles(Set.of("ROLE"))
        .email("EMAIL")
        .build());

    UserInfo result = mapper.apply(user, organizationRoles);

    Assertions.assertEquals(
        UserInfo.builder()
            .userId(user.getUserId())
            .mappedExternalUserId(user.getMappedExternalUserId())
            .fiscalCode(user.getFiscalCode())
            .familyName(user.getLastName())
            .name(user.getFirstName())
            .organizations(organizationRoles.stream()
                .map(r -> UserOrganizationRoles.builder()
                    .operatorId(r.getOperatorId())
                    .organizationIpaCode(r.getOrganizationIpaCode())
                    .roles(new ArrayList<>(r.getRoles()))
                    .email(r.getEmail())
                    .build())
                .toList())
            .build(),
        result
    );
  }
}