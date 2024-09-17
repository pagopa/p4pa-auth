package it.gov.pagopa.payhub.auth.mypay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayUser;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayUsersRepository;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MyPayUsersServiceTest {

  @Mock
  private MyPayUsersRepository myPayUsersRepositoryMock;
  @InjectMocks
  private MyPayUsersService myPayUsersService;

  @Test
  void whenUpdateUserThenCheckNewValues() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";
    String email = "EMAIL";
    String newLastName = "NEWLASTNAME";
    Date now = new Date();

    Optional<MyPayUser> existingUser = Optional.of(
        MyPayUser.builder()
            .codFedUserId(externalUserId)
            .codCodiceFiscaleUtente(fiscalCode)
            .deFirstname(firstName)
            .deLastname(lastName)
            .deEmailAddress(email)
            .dtUltimoLogin(now)
            .build());

    // Mock behavior
    when(myPayUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existingUser);

    // Act
    myPayUsersService.registerMyPayUser(externalUserId, fiscalCode, firstName, newLastName);

    // Assert
    verify(myPayUsersRepositoryMock).save(existingUser.get());
    assertEquals(newLastName,existingUser.get().getDeLastname());
  }

  @Test
  void whenRegisterMyPayUserThenVerifyNewUser() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    String fiscalCode = "FISCALCODE";
    String firstName = "FIRSTNAME";
    String lastName = "LASTNAME";

    Optional<MyPayUser> existedMyPayUser = Optional.empty();

    // Mock behavior (no existing user)
    Mockito.when(myPayUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existedMyPayUser);

    // Act
    myPayUsersService.registerMyPayUser(externalUserId, fiscalCode, firstName, lastName);

    //ArgumentMatcher to verify just userId due to lastLogin can be different
    ArgumentMatcher<MyPayUser> userMatcher = new ArgumentMatcher<MyPayUser>() {
      @Override
      public boolean matches(MyPayUser user) {
        return user.getCodFedUserId().equals(externalUserId);
      }
    };

    // Assert
    verify(myPayUsersRepositoryMock).save(argThat(userMatcher));
  }
}
