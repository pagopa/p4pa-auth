package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.user.registration.ExternalUserIdObfuscatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FakeUserInfoServiceTest {

    private FakeUserInfoService fakeUserInfoService;
    @Mock
    private UsersRepository usersRepositoryMock;
    @Mock
    private ExternalUserIdObfuscatorService externalUserIdObfuscatorServiceMock;
    @BeforeEach
    void init(){
        fakeUserInfoService = new FakeUserInfoService(usersRepositoryMock, externalUserIdObfuscatorServiceMock);
    }

    @Test
    void givenIamUserIdValidWhenBuildIamUserInfoFakeThenSuccess(){
        String iamUserId = "iam_userid";
        String subjectIssuer = "issuer";
        String mappedExternalUserId = "external_userid";
        User user = User.builder()
                .userId("userid")
                .userCode("usercode")
                .build();

        IamUserInfoDTO iamUserInfoFakeExpected = IamUserInfoDTO.builder()
                .userId(iamUserId)
                .innerUserId("userid")
                .name("fake")
                .familyName("user")
                .email("email@email.it")
                .fiscalCode("usercode")
                .issuer(subjectIssuer)
                .build();

        Mockito.when(externalUserIdObfuscatorServiceMock.obfuscate(iamUserId)).thenReturn(mappedExternalUserId);
        Mockito.when(usersRepositoryMock.findByMappedExternalUserId(mappedExternalUserId)).thenReturn(Optional.ofNullable(user));
        IamUserInfoDTO iamUserInfoFake = fakeUserInfoService.buildIamUserInfoFake(iamUserId, subjectIssuer);

        Assertions.assertEquals(iamUserInfoFake, iamUserInfoFakeExpected);
    }

    @Test
    void givenIamUserIdNotExistentWhenBuildIamUserInfoFakeThenException(){
        String iamUserId = "iam_userid_not_existent";
        String subjectIssuer = "issuer";
        String mappedExternalUserId = "external_userid_not_existent";

        Mockito.when(externalUserIdObfuscatorServiceMock.obfuscate(iamUserId)).thenReturn(mappedExternalUserId);
        Mockito.when(usersRepositoryMock.findByMappedExternalUserId(mappedExternalUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () ->
                fakeUserInfoService.buildIamUserInfoFake(iamUserId, subjectIssuer));

        Assertions.assertEquals("User with this mappedExternalUserId not found", exception.getMessage());

    }
}
