package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.user.UserService;
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
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    @Test
    void givenNoOrganizationAccessModeAndNoOrganizationDataWhenRegisterUserThenThrowInvalidOrganizationAccessDataException() {
        // Given
        init(false);
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();
        userInfoUserPair.getFirst().setOrganizationAccess(IamUserOrganizationRolesDTO.builder().organizationIpaCode("ORG2").build());

        // When
        service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
    }

    @Test
    void givenValidOrganizationAccessModeWhenRegisterUserThenOk() {
        // Given
        init(true);
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();

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
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();
        IamUserInfoDTO userInfo = userInfoUserPair.getFirst();
        userInfo.setOrganizationAccess(IamUserOrganizationRolesDTO.builder().organizationIpaCode("ORG2").build());

        // When
        InvalidOrganizationAccessDataException exception = Assertions.assertThrows(InvalidOrganizationAccessDataException.class, () -> service.registerUser(userInfo));

        // Then
        verifyRegisterUserInvocation(userInfo);
        Assertions.assertEquals("No roles configured for organizationAccess IamUserOrganizationRolesDTO(organizationIpaCode=ORG2, roles=null)", exception.getMessage());
    }

    private void verifyRegisterUserInvocation(IamUserInfoDTO userInfo) {
        Mockito.verify(userServiceMock).registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());
    }

    private void verifyRegisterOperatorInvocation(User user, String organizationIpaCode, Set<String> roles) {
        Mockito.verify(userServiceMock).registerOperator(user.getUserId(), organizationIpaCode, roles);
    }

    private Pair<IamUserInfoDTO, User> configureUserServiceMock() {
        IamUserInfoDTO userInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .issuer("IAMISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .roles(List.of("ROLE"))
                        .build())
                .build();

        User registeredUser = User.builder()
                .userId("INTERNALID")
                .build();
        Mockito.when(userServiceMock.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer()))
                .thenReturn(registeredUser);

        return Pair.of(userInfo, registeredUser);
    }
}
