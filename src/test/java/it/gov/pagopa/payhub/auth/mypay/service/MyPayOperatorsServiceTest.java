package it.gov.pagopa.payhub.auth.mypay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import it.gov.pagopa.payhub.auth.mypay.model.MyPayOperator;
import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MyPayOperatorsServiceTest {

  @Mock
  private MyPayOperatorsRepository myPayOperatorsRepositoryMock;

  @InjectMocks
  private MyPayOperatorsService myPayOperatorsService;

  @Test
  public void testRegisterMyPivotOperator_ExistingOperator_UpdatesRole() {
    // Arrange
    String mappedExternalUserId = "USERID";
    String organizationIpaCode = "IPACODE";
    String email = "EMAIL";
    Set<String> roles = Set.of("ROLE_ADMIN");
    Optional<MyPayOperator> existingOperator = Optional.of(
        MyPayOperator.builder()
            .codFedUserId(mappedExternalUserId)
            .codIpaEnte(organizationIpaCode)
            .build());

    // Mock behavior
    when(myPayOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode))
        .thenReturn(existingOperator.get());

    // Act
    myPayOperatorsService.registerMyPayOperator(mappedExternalUserId, email, organizationIpaCode, roles);

    // Assert
    verify(myPayOperatorsRepositoryMock).save(existingOperator.get());
    assertEquals("ROLE_ADMIN", existingOperator.get().getRuolo());
  }

  @Test
  public void testRegisterMyPivotOperator_NewOperator_CreatesNewEntry() {
    // Arrange
    String mappedExternalUserId = "USERID";
    String organizationIpaCode = "IPACODE";
    String email = "EMAIL";
    Set<String> roles = Set.of("ROLE_ADMIN");

    // Mock behavior (no existing operator)
    when(myPayOperatorsRepositoryMock.findByCodFedUserIdAndCodIpaEnte(mappedExternalUserId, organizationIpaCode)).thenReturn(null);

    // Act
    myPayOperatorsService.registerMyPayOperator(mappedExternalUserId, email, organizationIpaCode, roles);

    // Assert
    MyPayOperator expectedOperator = MyPayOperator.builder()
        .codFedUserId(mappedExternalUserId)
        .ruolo(roles.stream().findFirst().orElse(null)) // Handle potential empty role set
        .codIpaEnte(organizationIpaCode)
        .deEmailAddress(email)
        .build();
    verify(myPayOperatorsRepositoryMock).save(expectedOperator);
  }
}
