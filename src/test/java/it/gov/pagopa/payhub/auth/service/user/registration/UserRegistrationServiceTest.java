package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.mypay.service.MyPayUsersService;
import it.gov.pagopa.payhub.auth.mypivot.service.MyPivotUsersService;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private ExternalUserIdObfuscatorService externalUserIdObfuscatorServiceMock;
    @Mock
    private FiscalCodeObfuscatorService fiscalCodeObfuscatorServiceMock;
    @Mock
    private UsersRepository usersRepositoryMock;
    @Mock
    private MyPayUsersService myPayUsersServiceMock;
    @Mock
    private MyPivotUsersService myPivotUsersServiceMock;

    private UserRegistrationService service;

    @BeforeEach
    void init(){
        service = new UserRegistrationService(
                externalUserIdObfuscatorServiceMock,
                fiscalCodeObfuscatorServiceMock,
                usersRepositoryMock,
                myPayUsersServiceMock,
                myPivotUsersServiceMock);
    }

    @AfterEach
    void verifyNotMoreInvocation(){
        Mockito.verifyNoMoreInteractions(
                externalUserIdObfuscatorServiceMock,
                fiscalCodeObfuscatorServiceMock,
                usersRepositoryMock);
    }

    @Test
    void whenRegisterUserThenReturnStoredUser(){
        // Given
        String externalUserId = "EXTERNALUSERID";
        String obfuscatedExternalUserId = "OBFUSCATEDEXTERNALUSERID";
        String fiscalCode = "FISCALCODE";
        String obfuscatedFiscalCode = "OBFUSCATEDFISCALCODE";
        String iamIssuer = "IAMISSUER";
        String name = "NAME";
        String familyName = "FAMILYNAME";
        String email = "EMAIL";

        User user = User.builder()
                .mappedExternalUserId(obfuscatedExternalUserId)
                .userCode(obfuscatedFiscalCode)
                .iamIssuer(iamIssuer)
                .fiscalCode(fiscalCode)
                .firstName(name)
                .lastName(familyName)
                .email(email)
                .build();
        User storedUser = new User();

        Mockito.when(externalUserIdObfuscatorServiceMock.obfuscate(externalUserId)).thenReturn(obfuscatedExternalUserId);
        Mockito.when(fiscalCodeObfuscatorServiceMock.obfuscate(fiscalCode)).thenReturn(obfuscatedFiscalCode);
        Mockito.when(usersRepositoryMock.registerUser(user)).thenReturn(storedUser);

        // When
        User result = service.registerUser(externalUserId, fiscalCode, iamIssuer, name, familyName, email);

        // Then
        Assertions.assertSame(storedUser, result);
    }
}
