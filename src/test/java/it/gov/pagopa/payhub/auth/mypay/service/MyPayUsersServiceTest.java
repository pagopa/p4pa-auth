package it.gov.pagopa.payhub.auth.mypay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayUsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MyPayUsersServiceTest {

  @Mock
  private MyPayUsersRepository myPayUsersRepositoryMock;
  @InjectMocks
  private MyPayUsersService myPayUsersService;

  @Test
  void testRegisterMyPayUser_ExistingUser_UpdatesDetails() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String email = "EMAIL";
    String newEmail = "NEWEMAIL";

    Optional<MyPayUser> existingUser = Optional.of(
        MyPayUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .version(0)
            .deFirstname(firstName)
            .deLastname(lastName)
            .deEmailAddress(email)
            .build());

    // Mock behavior
    when(myPayUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existingUser.get());

    // Act
    myPayUsersService.registerMyPayUser(externalUserId, fiscalCode, firstName, lastName, newEmail);

    // Assert
    verify(myPayUsersRepositoryMock).save(existingUser.get());
    assertEquals(newEmail,existingUser.get().getDeEmailAddress());
  }

  @Test
  void testRegisterMyPayUser_NewUser_CreatesEntry() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String email = "EMAIL";

    // Mock behavior (no existing user)
    when(myPayUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(null);

    // Act
    myPayUsersService.registerMyPayUser(externalUserId, fiscalCode, firstName, lastName, email);

    Optional<MyPayUser> newUser = Optional.of(
        MyPayUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .version(0)
            .deFirstname(firstName)
            .deLastname(lastName)
            .deEmailAddress(email)
            .build());

    // Assert
    verify(myPayUsersRepositoryMock).save(newUser.get());
  }
}
