package it.gov.pagopa.payhub.auth.mypivot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotUser;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotUsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MyPivotUsersServiceTest {

  @Mock
  private MyPivotUsersRepository myPivotUsersRepositoryMock;
  @InjectMocks
  private MyPivotUsersService myPivotUsersService;

  @Test
  public void testRegisterMyPivotUser_ExistingUser_UpdatesDetails() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String email = "EMAIL";
    String newEmail = "NEWEMAIL";

    Optional<MyPivotUser> existingUser = Optional.of(
        MyPivotUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .version(0)
            .deFirstname(firstName)
            .deLastname(lastName)
            .deEmailAddress(email)
            .build());

    // Mock behavior
    when(myPivotUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existingUser.get());

    // Act
    myPivotUsersService.registerMyPivotUser(externalUserId, fiscalCode, firstName, lastName, newEmail);

    // Assert
    verify(myPivotUsersRepositoryMock).save(existingUser.get());
    assertEquals(newEmail,existingUser.get().getDeEmailAddress());
  }

  @Test
  public void testRegisterMyPivotUser_NewUser_CreatesEntry() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String email = "EMAIL";

    // Mock behavior (no existing user)
    when(myPivotUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(null);

    // Act
    myPivotUsersService.registerMyPivotUser(externalUserId, fiscalCode, firstName, lastName, email);

    Optional<MyPivotUser> newUser = Optional.of(
        MyPivotUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .version(0)
            .deFirstname(firstName)
            .deLastname(lastName)
            .deEmailAddress(email)
            .build());

    // Assert
    verify(myPivotUsersRepositoryMock).save(newUser.get());
  }
}
