package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private TokenStoreService tokenStoreServiceMock;

    private UserService service;

    @BeforeEach
    void init(){
        service = new UserServiceImpl(tokenStoreServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(tokenStoreServiceMock);
    }

    @Test
    void givenNotExistentTokenWhenGetUserInfoThenInvalidAccessTokenException(){
        // Given
        String accessToken = "accessToken";

        // When, Then
        Assertions.assertThrows(InvalidAccessTokenException.class, ()->service.getUserInfo(accessToken));

        Mockito.verify(tokenStoreServiceMock).load(accessToken);
    }

    @Test
    void givenAccessTokenWhenGetUserInfoThenOk(){
        // Given
        String accessToken = "accessToken";

        UserInfo expectedUserInfo = new UserInfo();
        Mockito.when(tokenStoreServiceMock.load(accessToken)).thenReturn(expectedUserInfo);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedUserInfo, result);
    }
}
