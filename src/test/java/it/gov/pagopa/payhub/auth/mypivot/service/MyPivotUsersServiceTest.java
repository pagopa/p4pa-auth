package it.gov.pagopa.payhub.auth.mypivot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotUser;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotUsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MyPivotUsersServiceTest {

  @Mock
  private MyPivotUsersRepository myPivotUsersRepositoryMock;
  @InjectMocks
  private MyPivotUsersService myPivotUsersService;

  @Test
  void whenUpdateUserThenCheckNewValues() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String newLastName = "NEWLASTNAME";

    Optional<MyPivotUser> existingUser = Optional.of(
        MyPivotUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .version(0)
            .deFirstname(firstName)
            .deLastname(lastName)
            .build());

    // Mock behavior
    when(myPivotUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existingUser);

    // Act
    myPivotUsersService.registerMyPivotUser(externalUserId, fiscalCode, firstName, newLastName);

    // Assert
    verify(myPivotUsersRepositoryMock).save(existingUser.get());
    assertEquals(newLastName,existingUser.get().getDeLastname());
  }

  @Test
  void whenRegisterMyPivotUserThenVerifyNewUser() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    Optional<MyPivotUser> existedMyPivotUser = Optional.empty();

    // Mock behavior (no existing user)
    when(myPivotUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existedMyPivotUser);

    // Act
    myPivotUsersService.registerMyPivotUser(externalUserId, fiscalCode, firstName, lastName);

    //ArgumentMatcher to verify just userId due to lastLogin can be different
    ArgumentMatcher<MyPivotUser> userMatcher = new ArgumentMatcher<MyPivotUser>() {
      @Override
      public boolean matches(MyPivotUser user) {
        return user.getCodFedUserId().equals(externalUserId);
      }
    };

    // Assert
    verify(myPivotUsersRepositoryMock).save(argThat(userMatcher));
  }
}
