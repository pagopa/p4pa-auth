package it.gov.pagopa.payhub.auth.mypay.service;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyPayOperatorsServiceTest {

  @Mock
  private MyPayOperatorsRepository myPayOperatorsRepositoryMock;

  @InjectMocks
  private MyPayOperatorsService myPayOperatorsService;

  @Test
  void whenUpdateRoleWithOperThenCheckBlankRole() {
    // Arrange
    Long operatorId = (long) 1;
    String mappedExternalUserId = "USERID";
    String organizationIpaCode = "IPACODE";
    String email = "EMAIL";
    Set<String> roles = Set.of("ROLE_OPER");

    MyPayOperator existingOperator = MyPayOperator.builder()
        .mygovOperatoreId(operatorId)
        .codFedUserId(mappedExternalUserId)
        .codIpaEnte(organizationIpaCode)
        .ruolo("ROLE_ADMIN")
        .build();

    // Mock behavior
    when(myPayOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode))
        .thenReturn(Optional.of(existingOperator));

    // Act
    myPayOperatorsService.registerMyPayOperator(mappedExternalUserId, email, organizationIpaCode, roles);

    // Assert
    verify(myPayOperatorsRepositoryMock).save(existingOperator);
    assertNull(existingOperator.getRuolo());
  }

  @Test
  void whenRegisterMyPayOperatorThenVerifyNewOperator() {
    // Arrange
    String mappedExternalUserId = "USERNID";
    String organizationIpaCode = "IPACODE";
    Set<String> roles = Set.of("ROLE_ADMIN");
    String email = "EMAIL";
    Optional<MyPayOperator> newOperator = Optional.empty();

    // Mock behavior (no existing operator)
    when(myPayOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode)).thenReturn(newOperator);

    // Act
    myPayOperatorsService.registerMyPayOperator(mappedExternalUserId, email, organizationIpaCode, roles);

    // Assert
    MyPayOperator expectedOperator = MyPayOperator.builder()
        .codFedUserId(mappedExternalUserId)
        .ruolo(roles.contains(Constants.ROLE_ADMIN)? Constants.ROLE_ADMIN : null)
        .codIpaEnte(organizationIpaCode)
        .deEmailAddress(email)
        .build();
    verify(myPayOperatorsRepositoryMock).save(expectedOperator);
  }
}
