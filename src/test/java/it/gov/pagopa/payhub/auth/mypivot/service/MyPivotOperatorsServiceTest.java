package it.gov.pagopa.payhub.auth.mypivot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypivot.model.MyPivotOperator;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MyPivotOperatorsServiceTest {

  @Mock
  private MyPivotOperatorsRepository myPivotOperatorsRepositoryMock;

  @InjectMocks
  private MyPivotOperatorsService myPivotOperatorsService;

  @Test
  public void testRegisterMyPivotOperator_ExistingOperator_UpdatesRole() {
    // Arrange
    String mappedExternalUserId = "USERID";
    String organizationIpaCode = "IPACODE";
    Set<String> roles = Set.of("ROLE_ADMIN");
    Optional<MyPivotOperator> existingOperator = Optional.of(
        MyPivotOperator.builder()
            .codFedUserId(mappedExternalUserId)
            .codIpaEnte(organizationIpaCode)
            .build());

    // Mock behavior
    when(myPivotOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode))
        .thenReturn(existingOperator.get());

    // Act
    myPivotOperatorsService.registerMyPivotOperator(mappedExternalUserId, organizationIpaCode, roles);

    // Assert
    verify(myPivotOperatorsRepositoryMock).save(existingOperator.get());
    assertEquals("ROLE_ADMIN", existingOperator.get().getRuolo());
  }

  @Test
  public void testRegisterMyPivotOperator_NewOperator_CreatesNewEntry() {
    // Arrange
    String mappedExternalUserId = "USERNID";
    String organizationIpaCode = "IPACODE";
    Set<String> roles = Set.of("ROLE_ADMIN");

    // Mock behavior (no existing operator)
    when(myPivotOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode)).thenReturn(null);

    // Act
    myPivotOperatorsService.registerMyPivotOperator(mappedExternalUserId, organizationIpaCode, roles);

    // Assert
    MyPivotOperator expectedOperator = MyPivotOperator.builder()
        .codFedUserId(mappedExternalUserId)
        .ruolo(roles.stream().findFirst().orElse(null))
        .codIpaEnte(organizationIpaCode)
        .build();
    verify(myPivotOperatorsRepositoryMock).save(expectedOperator);
  }
}
