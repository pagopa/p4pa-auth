package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class IamUserRegistrationServiceTest {

    @Mock
    private UserService userServiceMock;

    private IamUserRegistrationService service;

    void init(boolean isOrganizationAccessMode) {
        service = new IamUserRegistrationService(isOrganizationAccessMode, userServiceMock);
    }

    @AfterEach
    void verifyNotMoreInvocation() {
        Mockito.verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    void givenNoOrganizationAccessModeWhenRegisterUserThenOk() {
        // Given
        init(false);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    @Test
    void givenNoOrganizationAccessModeAndNoOrganizationDataWhenRegisterUserThenThrowInvalidOrganizationAccessDataException() {
        // Given
        init(false);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();
        userInfoUserPair.getFirst().setOrganizations(List.of(UserOrganizationRoles.builder().ipaCode("ORG2").build()));

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    @Test
    void givenValidOrganizationAccessModeWhenRegisterUserThenOk() {
        // Given
        init(true);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
        verifyRegisterOperatorInvocation(userInfoUserPair.getSecond(), "ORG", Set.of("ROLE"));
    }

    @Test
    void givenInvalidOrganizationAccessModeWhenRegisterUserThenThrowInvalidOrganizationAccessDataException() {
        // Given
        init(true);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();
        UserInfo userInfo = userInfoUserPair.getFirst();
        userInfo.setOrganizations(List.of(UserOrganizationRoles.builder().ipaCode("ORG2").build()));

        // When
        InvalidOrganizationAccessDataException exception = Assertions.assertThrows(InvalidOrganizationAccessDataException.class, () -> service.registerUser(userInfo));

        // Then
        verifyRegisterUserInvocation(userInfo);
        Assertions.assertEquals("No roles configured for organizationAccess ORG; organizations: [UserOrganizationRoles(id=null, name=null, fiscalCode=null, ipaCode=ORG2, roles=null)]", exception.getMessage());
    }

    private void verifyRegisterUserInvocation(UserInfo userInfo) {
        Mockito.verify(userServiceMock).registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());
    }

    private void verifyRegisterOperatorInvocation(User user, String organizationIpaCode, Set<String> roles) {
        Mockito.verify(userServiceMock).registerOperator(user.getUserId(), organizationIpaCode, roles);
    }

    private Pair<UserInfo, User> configureUserServiceMock() {
        UserInfo userInfo = UserInfo.builder()
                .userId("EXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .issuer("IAMISSUER")
                .organizationAccess("ORG")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .ipaCode("ORG")
                        .roles(List.of("ROLE"))
                        .build()))
                .build();

        User registeredUser = User.builder()
                .userId("INTERNALID")
                .build();
        Mockito.when(userServiceMock.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer()))
                .thenReturn(registeredUser);

        return Pair.of(userInfo, registeredUser);
    }
}
