package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

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
    void givenNoOrganizationAccessModewhenRegisterUserThenOk() {
        // Given
        init(false);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    @Test
    void givenOrganizationAccessModeWhenRegisterUserThenOk() {
        // Given
        init(true);
        Pair<UserInfo, User> userInfoUserPair = configureUserServiceMock();

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    private void verifyRegisterUserInvocation(UserInfo userInfo) {
        Mockito.verify(userServiceMock).registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());
    }

    private Pair<UserInfo, User> configureUserServiceMock() {
        UserInfo userInfo = UserInfo.builder()
                .userId("EXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .issuer("IAMISSUER")
                .build();

        User registeredUser = new User();
        Mockito.when(userServiceMock.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer()))
                .thenReturn(registeredUser);

        return Pair.of(userInfo, registeredUser);
    }
}
