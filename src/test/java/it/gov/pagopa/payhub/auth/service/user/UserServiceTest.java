package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.OperatorRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OrganizationOperatorRetrieverService;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private TokenStoreService tokenStoreServiceMock;
    @Mock
    private UserRegistrationService userRegistrationServiceMock;
    @Mock
    private OperatorRegistrationService operatorRegistrationServiceMock;
    @Mock
    private IamUserInfoDTO2UserInfoMapper userInfoMapperMock;
    @Mock
    private OrganizationOperatorRetrieverService organizationOperatorRetrieverServiceMock;

    private UserService service;

    @BeforeEach
    void init() {
        service = new UserServiceImpl(tokenStoreServiceMock, userRegistrationServiceMock, operatorRegistrationServiceMock, userInfoMapperMock, organizationOperatorRetrieverServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                tokenStoreServiceMock,
                userRegistrationServiceMock,
                operatorRegistrationServiceMock,
                userInfoMapperMock,
                organizationOperatorRetrieverServiceMock);
    }

    @Test
    void givenNotExistentTokenWhenGetUserInfoThenInvalidAccessTokenException() {
        // Given
        String accessToken = "accessToken";

        // When, Then
        Assertions.assertThrows(InvalidAccessTokenException.class, () -> service.getUserInfo(accessToken));

        Mockito.verify(tokenStoreServiceMock).load(accessToken);
    }

    @Test
    void givenAccessTokenWhenGetUserInfoThenOk() {
        // Given
        String accessToken = "accessToken";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        UserInfo expectedUserInfo = new UserInfo();
        Mockito.when(tokenStoreServiceMock.load(accessToken)).thenReturn(iamUserInfo);
        Mockito.when(userInfoMapperMock.apply(iamUserInfo)).thenReturn(expectedUserInfo);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedUserInfo, result);
    }

    @Test
    void whenRegisterUserThenReturnStoredUser() {
        // Given
        String externalUserId = "EXTERNALUSERID";
        String fiscalCode = "FISCALCODE";
        String iamIssuer = "IAMISSUER";
        String name = "NAME";
        String familyName = "FAMILYNAME";
        String email = "EMAIL";
        User storedUser = new User();

        Mockito.when(userRegistrationServiceMock.registerUser(externalUserId, fiscalCode, iamIssuer, name, familyName, email))
                .thenReturn(storedUser);

        // When
        User result = service.registerUser(externalUserId, fiscalCode, iamIssuer, name, familyName, email);

        // Then
        Assertions.assertSame(storedUser, result);
    }

    @Test
    void whenRegisterOperatorThenReturnStoredOperator() {
        // Given
        String userId = "USERID";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();

        Mockito.when(operatorRegistrationServiceMock.registerOperator(userId, organizationIpaCode, roles))
                .thenReturn(storedOperator);

        // When
        Operator result = service.registerOperator(userId, organizationIpaCode, roles);

        // Then
        Assertions.assertSame(storedOperator, result);
    }

    @Test
    void whenRetrieveOrganizationOperatorsThenReturnOperatorList(){
        // Given
        String organizationIpaCode = "IPACODE";

        List<OperatorDTO> expectedOperators = Collections.emptyList();
        Mockito.when(organizationOperatorRetrieverServiceMock.retrieveOrganizationOperators(organizationIpaCode)).thenReturn(expectedOperators);

        // When
        List<OperatorDTO> result = service.retrieveOrganizationOperators(organizationIpaCode);

        // Then
        Assertions.assertSame(expectedOperators, result);
    }
}
