package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserDTOMapper {
  public UserDTO map(User user) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .iamIssuer(user.getIamIssuer())
        .mappedExternalUserId(user.getMappedExternalUserId())
        .userCode(user.getUserCode())
        .fiscalCode(user.getFiscalCode())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .build();
  }
}
