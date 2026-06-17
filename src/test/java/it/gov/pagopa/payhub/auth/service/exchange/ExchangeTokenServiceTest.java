package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenServiceTest {

    @Mock
    private ValidateExternalTokenService validateExternalTokenServiceMock;
    @Mock
    private AccessTokenBuilderService accessTokenBuilderServiceMock;
    @Mock
    private TokenStoreService tokenStoreServiceMock;
    @Mock
    private IDTokenClaims2UserInfoMapper idTokenClaimsMapperMock;
    @Mock
    private IamUserRegistrationService iamUserRegistrationServiceMock;
    @Mock
    private FakeUserInfoService fakeUserInfoServiceMock;

    private ExchangeTokenService service;

    @BeforeEach
    void init(){
        service = new ExchangeTokenServiceImpl(
                "DEV",
                validateExternalTokenServiceMock,
                accessTokenBuilderServiceMock,
                tokenStoreServiceMock,
                idTokenClaimsMapperMock,
                iamUserRegistrationServiceMock, 
                fakeUserInfoServiceMock);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                validateExternalTokenServiceMock,
                accessTokenBuilderServiceMock,
                tokenStoreServiceMock,
                idTokenClaimsMapperMock,
                iamUserRegistrationServiceMock
        );
    }

    @Test
    void givenValidTokenWhenPostTokenThenSuccess(){
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        HashMap<String, Claim> expectedClaims = new HashMap<>();
        Mockito.when(validateExternalTokenServiceMock.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedClaims);

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        Mockito.when(idTokenClaimsMapperMock.apply(expectedClaims))
                .thenReturn(iamUserInfo);

        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(iamUserRegistrationServiceMock.registerUser(Mockito.same(iamUserInfo)))
                .thenReturn(expectedAccessToken);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
    }

    @Test
    void givenValidTokenFakeWhenPostTokenThenSuccess() {
        // Given
        String clientId = "CLIENT_ID";
        String subjectToken = "SUBJECT_TOKEN";
        String subjectIssuer = "SUBJECT_ISSUER";
        String subjectTokenType = "FAKE-AUTH";
        String scope = "SCOPE";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        Mockito.when(fakeUserInfoServiceMock.buildIamUserInfoFake(subjectToken, subjectIssuer))
                .thenReturn(iamUserInfo);

        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(iamUserInfo))
                .thenReturn(expectedAccessToken);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Mockito.verifyNoInteractions(validateExternalTokenServiceMock, idTokenClaimsMapperMock, iamUserRegistrationServiceMock);
    }

    @Test
    void givenNotAllowedEnvAndFakeTokenWhenPostTokenThenSkipFakeHandling() {
        // Given
        String clientId = "CLIENT_ID";
        String subjectToken = "SUBJECT_TOKEN";
        String subjectIssuer = "SUBJECT_ISSUER";
        String subjectTokenType = "FAKE-AUTH";
        String scope = "SCOPE";

        InvalidTokenException expectedException = new InvalidTokenException("ERRORCODE", "DUMMY");
        Mockito.when(validateExternalTokenServiceMock.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenThrow(expectedException);

        service = new ExchangeTokenServiceImpl(
                "PROD",
                validateExternalTokenServiceMock,
                accessTokenBuilderServiceMock,
                tokenStoreServiceMock,
                idTokenClaimsMapperMock,
                iamUserRegistrationServiceMock,
                fakeUserInfoServiceMock);

        // When
        InvalidTokenException result = Assertions.assertThrows(InvalidTokenException.class, () -> service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope));

        // Then
        Assertions.assertSame(expectedException, result);
    }
}
