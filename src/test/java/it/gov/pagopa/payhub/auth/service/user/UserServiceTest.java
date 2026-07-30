package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.OperatorRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OrganizationOperatorRetrieverService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.UserInfoRetrieverService;
import it.gov.pagopa.payhub.dto.generated.LimitedScopeResource;
import it.gov.pagopa.payhub.dto.generated.OperatorDTO;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    @Mock
    private UserInfoRetrieverService userInfoRetrieverServiceMock;
    @InjectMocks
    private UserServiceImpl service;

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                tokenStoreServiceMock,
                userRegistrationServiceMock,
                operatorRegistrationServiceMock,
                userInfoMapperMock,
                organizationOperatorRetrieverServiceMock,
                userInfoRetrieverServiceMock);
    }

    @Test
    void givenNotExistentTokenWhenGetUserInfoThenInvalidAccessTokenException() {
        // Given
        String accessToken = "accessToken";

        // When, Then
        Assertions.assertThrows(InvalidAccessTokenException.class, () -> service.getUserInfo(accessToken));

        verify(tokenStoreServiceMock).load(accessToken);
    }

    @Test
    void givenAccessTokenWhenGetUserInfoThenOk() {
        // Given
        String accessToken = "accessToken";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        UserInfo expectedUserInfo = new UserInfo();
        Mockito.when(tokenStoreServiceMock.load(accessToken)).thenReturn(iamUserInfo);
        Mockito.when(userInfoMapperMock.apply(iamUserInfo, accessToken)).thenReturn(expectedUserInfo);

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
        User storedUser = new User();

        Mockito.when(userRegistrationServiceMock.registerUser(externalUserId, fiscalCode, iamIssuer, name, familyName))
                .thenReturn(storedUser);

        // When
        User result = service.registerUser(externalUserId, fiscalCode, iamIssuer, name, familyName);

        // Then
        Assertions.assertSame(storedUser, result);
    }

    @Test
    void whenRegisterOperatorThenReturnStoredOperator() {
        // Given
        String accessToken = "ACCESS_TOKEN";
        String organizationIpaCode = "ORGANIZATIONIPACODE";
        String email = "EMAIL";
        Set<String> roles = Set.of("ROLE");
        Operator storedOperator = new Operator();
        User user = new User();
        user.setUserId("USERID");

        Mockito.when(operatorRegistrationServiceMock.registerOperator(user, organizationIpaCode, roles, email, null, accessToken))
                .thenReturn(storedOperator);

        // When
        Operator result = service.registerOperator(user, organizationIpaCode, roles, email, null, accessToken);

        // Then
        Assertions.assertSame(storedOperator, result);
    }

    @Test
    void whenRetrieveOrganizationOperatorsThenReturnOperatorList(){
        // Given
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0,1);

        Page<OperatorDTO> expectedOperators = new PageImpl<>(Collections.emptyList());
        Mockito.when(organizationOperatorRetrieverServiceMock.retrieveOrganizationOperators(organizationIpaCode, pageRequest)).thenReturn(expectedOperators);

        // When
        Page<OperatorDTO> result = service.retrieveOrganizationOperators(organizationIpaCode, pageRequest);

        // Then
        Assertions.assertSame(expectedOperators, result);
    }

    @Test
    void whenGetUserInfoFromMappedExternalUserIdThenOk(){
        // Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";
        UserInfo expectedResult = new UserInfo();

        Mockito.when(userInfoRetrieverServiceMock.findByMappedExternalUserId(mappedExternalUserId, accessToken))
                .thenReturn(expectedResult);

        // When
        UserInfo result = service.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenAccessTokenLimitedSingleUsageWhenGetUserInfoThenOk() {
        // Given
        String accessToken = "accessToken";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        UserInfoLimitedScope expectedUserInfo = new UserInfoLimitedScope();
        expectedUserInfo.setResource(LimitedScopeResource.builder()
                .singleUsage(Boolean.TRUE)
                .build());
        Mockito.when(tokenStoreServiceMock.load(accessToken)).thenReturn(iamUserInfo);
        Mockito.when(userInfoMapperMock.apply(iamUserInfo, accessToken)).thenReturn(expectedUserInfo);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedUserInfo, result);
        verify(tokenStoreServiceMock).delete(accessToken);
    }

    @Test
    void givenAccessTokenLimitedNullSingleUsageWhenGetUserInfoThenOk() {
        // Given
        String accessToken = "accessToken";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        UserInfoLimitedScope expectedUserInfo = new UserInfoLimitedScope();
        expectedUserInfo.setResource(LimitedScopeResource.builder()
                .build());
        Mockito.when(tokenStoreServiceMock.load(accessToken)).thenReturn(iamUserInfo);
        Mockito.when(userInfoMapperMock.apply(iamUserInfo, accessToken)).thenReturn(expectedUserInfo);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedUserInfo, result);
        verify(tokenStoreServiceMock, never()).delete(accessToken);
    }
}
