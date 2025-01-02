package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import org.apache.commons.lang3.tuple.Pair;
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

        User registeredUser = User.builder().userId("INNERUSERID").mappedExternalUserId("MAPPEDEXTERNALUSERID").build();
        Mockito.when(iamUserRegistrationServiceMock.registerUser(Mockito.same(iamUserInfo)))
                .thenReturn(registeredUser);

        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(registeredUser.getMappedExternalUserId(), iamUserInfo))
                .thenReturn(expectedAccessToken);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Assertions.assertEquals(registeredUser.getUserId(), iamUserInfo.getInnerUserId());
    }

    @Test
    void givenValidTokenFakeWhenPostTokenThenSuccess() {
        // Given
        String clientId = "CLIENT_ID";
        String subjectToken = "SUBJECT_TOKEN";
        String subjectIssuer = "SUBJECT_ISSUER";
        String subjectTokenType = "FAKE-AUTH";
        String scope = "SCOPE";

        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        Mockito.when(fakeUserInfoServiceMock.buildIamUserInfoFake(subjectToken, subjectIssuer))
                .thenReturn(Pair.of(mappedExternalUserId, iamUserInfo));

        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(mappedExternalUserId, iamUserInfo))
                .thenReturn(expectedAccessToken);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Mockito.verifyNoInteractions(validateExternalTokenServiceMock, idTokenClaimsMapperMock, iamUserRegistrationServiceMock);
    }
}
