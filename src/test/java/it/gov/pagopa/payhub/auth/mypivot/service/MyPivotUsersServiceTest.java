package it.gov.pagopa.payhub.auth.mypivot.service;

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
  void whenRegisterMyPivotUserThenVerifyNewUser() {
    // Arrange
    String externalUserId = "EXTERNALUSERID";
    Optional<MyPivotUser> existedMyPivotUser = Optional.empty();

    // Mock behavior (no existing user)
    when(myPivotUsersRepositoryMock.findByCodFedUserId(externalUserId)).thenReturn(existedMyPivotUser);

    // Act
    myPivotUsersService.registerMyPivotUser(externalUserId);

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
