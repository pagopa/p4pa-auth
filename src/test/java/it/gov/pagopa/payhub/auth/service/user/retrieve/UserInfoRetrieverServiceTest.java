package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.mapper.Client2UserInfoMapper;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.user.IamUserInfoDTO2UserInfoMapper;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserInfoRetrieverServiceTest {

    @Mock
    private UsersRepository usersRepositoryMock;
    @Mock
    private ClientRepository clientRepositoryMock;
    @Mock
    private ClientMapper clientMapperMock;
    @Mock
    private Client2UserInfoMapper client2UserInfoMapperMock;
    @Mock
    private IamUserInfoDTO2UserInfoMapper iamUserInfoMapperMock;

    private UserInfoRetrieverService service;

    @BeforeEach
    void init() {
        service = new UserInfoRetrieverService(
                usersRepositoryMock,
                clientRepositoryMock,
                clientMapperMock,
                client2UserInfoMapperMock,
                iamUserInfoMapperMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                usersRepositoryMock,
                clientRepositoryMock,
                clientMapperMock,
                client2UserInfoMapperMock,
                iamUserInfoMapperMock);
    }

    //region system user
    @Test
    void givenSystemUserWhenFindByMappedExternalUserIdThenOk() {
        // Given
        String clientId = "CLIENTID";
        String accessToken = "accessToken";
        Client client = new Client();
        ClientNoSecretDTO clientNoSecretDTO = new ClientNoSecretDTO();
        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        UserInfo expectedResult = new UserInfo();

        Mockito.when(clientRepositoryMock.findById(clientId))
                .thenReturn(Optional.of(client));
        Mockito.when(clientMapperMock.mapToNoSecretDTO(client))
                .thenReturn(clientNoSecretDTO);
        Mockito.when(client2UserInfoMapperMock.apply(clientNoSecretDTO))
                .thenReturn(iamUserInfo);

        Mockito.when(iamUserInfoMapperMock.apply(iamUserInfo, accessToken))
                .thenReturn(expectedResult);

        // When
        UserInfo result = service.findByMappedExternalUserId("WS_USER-" + clientId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentSystemUserWhenFindByMappedExternalUserIdThenUserNotFoundException() {
        // Given
        String clientId = "CLIENTID";
        String accessToken = "accessToken";

        Mockito.when(clientRepositoryMock.findById(clientId))
                .thenReturn(Optional.empty());

        // When
        Assertions.assertThrows(UserNotFoundException.class, () -> service.findByMappedExternalUserId("WS_USER-" + clientId, accessToken));
    }
//endregion

    //region regular user
    @Test
    void givenRegularUserWhenFindByMappedExternalUserIdThenOk() {
        // Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        String accessToken = "accessToken";
        UserInfo expectedResult = new UserInfo();

        User user = User.builder()
                .iamIssuer("IAMISSUER")
                .userId("USERID")
                .mappedExternalUserId(mappedExternalUserId)
                .userCode("USERCODE")
                .fiscalCode("FISCALCODE")
                .firstName("FIRSTNAME")
                .lastName("LASTNAME")
                .build();
        IamUserInfoDTO iamUserInfo = IamUserInfoDTO.builder()
                .issuer("IAMISSUER")
                .userId("USERID")
                .innerUserId("USERID")
                .mappedExternalUserId(mappedExternalUserId)
                .fiscalCode("FISCALCODE")
                .name("FIRSTNAME")
                .familyName("LASTNAME")
                .systemUser(false)
                .build();

        Mockito.when(usersRepositoryMock.findByMappedExternalUserId(mappedExternalUserId))
                .thenReturn(Optional.of(user));


        Mockito.when(iamUserInfoMapperMock.apply(iamUserInfo, accessToken))
                .thenReturn(expectedResult);

        // When
        UserInfo result = service.findByMappedExternalUserId(mappedExternalUserId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentRegularUserWhenFindByMappedExternalUserIdThenUserNotFoundException() {
        // Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        String accessToken = "accessToken";

        Mockito.when(usersRepositoryMock.findByMappedExternalUserId(mappedExternalUserId))
                .thenReturn(Optional.empty());

        // When
        Assertions.assertThrows(UserNotFoundException.class, () -> service.findByMappedExternalUserId(mappedExternalUserId, accessToken));
    }
//endregion
}
