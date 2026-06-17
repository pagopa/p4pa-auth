package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
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
    @Mock
    private AccessTokenBuilderService accessTokenBuilderServiceMock;
    @Mock
    private TokenStoreService tokenStoreServiceMock;

    private IamUserRegistrationService service;

    void init(boolean isOrganizationAccessMode) {
        service = new IamUserRegistrationService(isOrganizationAccessMode, userServiceMock, accessTokenBuilderServiceMock, tokenStoreServiceMock);
    }

    @AfterEach
    void verifyNotMoreInvocation() {
        Mockito.verifyNoMoreInteractions(
                userServiceMock,
                accessTokenBuilderServiceMock,
                tokenStoreServiceMock
        );
    }

    @Test
    void givenNoOrganizationAccessModeWhenRegisterUserThenOk() {
        // Given
        init(false);
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();

        AccessToken expectedAccessToken = new AccessToken();
        expectedAccessToken.setAccessToken("ACCESS_TOKEN");
        Mockito.when(accessTokenBuilderServiceMock.build(userInfoUserPair.getFirst()))
                .thenReturn(expectedAccessToken);
        Mockito.when(tokenStoreServiceMock.save(expectedAccessToken.getAccessToken(), userInfoUserPair.getFirst()))
                .thenReturn(userInfoUserPair.getFirst());
        // When
        AccessToken actualAccessToken = service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
        Assertions.assertEquals(expectedAccessToken, actualAccessToken);
    }

    @Test
    void givenNoOrganizationAccessModeAndNoOrganizationDataWhenRegisterUserThenThrowInvalidOrganizationAccessDataException() {
        // Given
        init(false);
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();
        userInfoUserPair.getFirst().setOrganizationAccess(IamUserOrganizationRolesDTO.builder().organizationIpaCode("ORG2").build());

        AccessToken expectedAccessToken = new AccessToken();
        expectedAccessToken.setAccessToken("ACCESS_TOKEN");
        Mockito.when(accessTokenBuilderServiceMock.build(userInfoUserPair.getFirst()))
                .thenReturn(expectedAccessToken);
        Mockito.when(tokenStoreServiceMock.save(expectedAccessToken.getAccessToken(), userInfoUserPair.getFirst()))
                .thenReturn(userInfoUserPair.getFirst());

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

        AccessToken expectedAccessToken = new AccessToken();
        expectedAccessToken.setAccessToken("ACCESS_TOKEN");
        Mockito.when(accessTokenBuilderServiceMock.build(userInfoUserPair.getFirst()))
                .thenReturn(expectedAccessToken);
        Mockito.when(tokenStoreServiceMock.save(expectedAccessToken.getAccessToken(), userInfoUserPair.getFirst()))
                .thenReturn(userInfoUserPair.getFirst());

        // When
        AccessToken actualAccessToken = service.registerUser(userInfoUserPair.getFirst());

        // Then
        verifyRegisterUserInvocation(userInfoUserPair.getFirst());
        verifyRegisterOperatorInvocation(userInfoUserPair.getSecond(), "ORG", "EMAIL", Set.of("ROLE"), "ACCESS_TOKEN");
        Assertions.assertSame(expectedAccessToken, actualAccessToken);
    }

    @Test
    void givenInvalidOrganizationAccessModeWhenRegisterUserThenThrowInvalidOrganizationAccessDataException() {
        // Given
        init(true);
        Pair<IamUserInfoDTO, User> userInfoUserPair = configureUserServiceMock();
        IamUserInfoDTO userInfo = userInfoUserPair.getFirst();
        userInfo.setOrganizationAccess(IamUserOrganizationRolesDTO.builder().organizationIpaCode("ORG2").build());

        AccessToken expectedAccessToken = new AccessToken();
        expectedAccessToken.setAccessToken("ACCESS_TOKEN");
        Mockito.when(accessTokenBuilderServiceMock.build(userInfoUserPair.getFirst()))
                .thenReturn(expectedAccessToken);
        Mockito.when(tokenStoreServiceMock.save(expectedAccessToken.getAccessToken(), userInfoUserPair.getFirst()))
                .thenReturn(userInfoUserPair.getFirst());

        // When
        InvalidOrganizationAccessDataException exception = Assertions.assertThrows(InvalidOrganizationAccessDataException.class, () -> service.registerUser(userInfo));

        // Then
        verifyRegisterUserInvocation(userInfo);
        Assertions.assertEquals("ROLES_NOT_FOUND",exception.getCode());
        Assertions.assertEquals("No roles configured for organizationAccess IamUserOrganizationRolesDTO(organizationIpaCode=ORG2, roles=[], email=null)", exception.getMessage());
    }

    private void verifyRegisterUserInvocation(IamUserInfoDTO userInfo) {
        Mockito.verify(userServiceMock).registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer(), userInfo.getName(), userInfo.getFamilyName());
    }

    private void verifyRegisterOperatorInvocation(User user, String organizationIpaCode, String email, Set<String> roles, String accessToken) {
        Mockito.verify(userServiceMock).registerOperator(user, organizationIpaCode, roles, email, accessToken);
    }

    private Pair<IamUserInfoDTO, User> configureUserServiceMock() {
        IamUserInfoDTO userInfo = IamUserInfoDTO.builder()
                .userId("EXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .name("NAME")
                .familyName("FAMILYNAME")
                .issuer("IAMISSUER")
                .organizationAccess(IamUserOrganizationRolesDTO.builder()
                        .organizationIpaCode("ORG")
                        .roles(List.of("ROLE"))
                        .email("EMAIL")
                        .build())
                .build();

        User registeredUser = User.builder()
                .userId("INTERNALID")
                .build();
        Mockito.when(userServiceMock.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer(), userInfo.getName(), userInfo.getFamilyName()))
                .thenReturn(registeredUser);

        return Pair.of(userInfo, registeredUser);
    }
}
