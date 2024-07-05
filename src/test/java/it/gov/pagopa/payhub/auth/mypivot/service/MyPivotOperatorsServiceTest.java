package it.gov.pagopa.payhub.auth.mypivot.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPivotOperatorsServiceTest {

  @Mock
  private MyPivotOperatorsRepository myPivotOperatorsRepositoryMock;

  @InjectMocks
  private MyPivotOperatorsService myPivotOperatorsService;

  @Test
  void whenUpdateRoleWithOperThenCheckBlankRole() {
    // Arrange
    Long operatorId = (long) 1;
    String mappedExternalUserId = "USERID";
    String organizationIpaCode = "IPACODE";
    Set<String> roles = Set.of("ROLE_OPER");

    MyPivotOperator existingOperator = MyPivotOperator.builder()
        .mygovOperatoreId(operatorId)
        .codFedUserId(mappedExternalUserId)
        .codIpaEnte(organizationIpaCode)
        .ruolo("ROLE_ADMIN")
        .build();

    // Mock behavior
    when(myPivotOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode))
        .thenReturn(Optional.of(existingOperator));

    // Act
    myPivotOperatorsService.registerMyPivotOperator(mappedExternalUserId, organizationIpaCode, roles);

    // Assert
    verify(myPivotOperatorsRepositoryMock).save(existingOperator);
    assertNull(existingOperator.getRuolo());
  }

  @Test
  void whenRegisterMyPivotOperatorThenVerifyNewOperator() {
    // Arrange
    String mappedExternalUserId = "USERNID";
    String organizationIpaCode = "IPACODE";
    Set<String> roles = Set.of("ROLE_ADMIN");
    Optional<MyPivotOperator> newOperator = Optional.empty();
    // Mock behavior (no existing operator)

    when(myPivotOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode)).thenReturn(newOperator);

    // Act
    myPivotOperatorsService.registerMyPivotOperator(mappedExternalUserId, organizationIpaCode, roles);

    // Assert
    MyPivotOperator expectedOperator = MyPivotOperator.builder()
        .codFedUserId(mappedExternalUserId)
        .ruolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null)
        .codIpaEnte(organizationIpaCode)
        .build();
    verify(myPivotOperatorsRepositoryMock).save(expectedOperator);
  }
}
