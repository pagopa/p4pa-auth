package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserDTOMapperTest {

  private final UserDTOMapper userDTOMapper = new UserDTOMapper();

  @Test
  void givenUserDTOWhenMapThenGetUser() {
    // Given
    User user = User.builder()
        .mappedExternalUserId("EXTERNALUSERID")
        .fiscalCode("FISCALCODE")
        .firstName("FIRSTNAME")
        .lastName("LASTNAME")
        .build();

    // When
    UserDTO result = userDTOMapper.map(user);

    // Then
    Assertions.assertEquals(
        UserDTO.builder()
            .externalUserId("EXTERNALUSERID")
            .fiscalCode("FISCALCODE")
            .firstName("FIRSTNAME")
            .lastName("LASTNAME")
            .build(), result
    );
  }
}