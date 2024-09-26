package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

@Service
public class Operator2UserInfoMapper implements BiFunction<User, List<Operator>, UserInfo> {

  @Override
  public UserInfo apply(User user, List<Operator> operator) {

    return UserInfo.builder()
        .userId(user.getUserId())
        .mappedExternalUserId(user.getMappedExternalUserId())
        .fiscalCode(user.getFiscalCode())
        .familyName(user.getLastName())
        .name(user.getFirstName())
        .organizations(operator.stream()
            .map(r -> UserOrganizationRoles.builder()
                .operatorId(r.getOperatorId())
                .organizationIpaCode(r.getOrganizationIpaCode())
                .roles(new ArrayList<>(r.getRoles()))
                .email(r.getEmail())
                .build())
            .toList())
        .build();
  }
}
