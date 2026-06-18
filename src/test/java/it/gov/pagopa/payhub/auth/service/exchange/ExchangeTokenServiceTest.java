package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    void givenNoTechnicalAccessTokenWhenPostTokenThenSuccess(){
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
        AccessToken technicalAccessToken = AccessToken.builder().accessToken("technicalAccessToken").expiresIn(3600).build();
        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(iamUserInfo))
                .thenReturn(technicalAccessToken)
                .thenReturn(expectedAccessToken);

        Mockito.when(iamUserRegistrationServiceMock.registerUser(iamUserInfo, technicalAccessToken.getAccessToken()))
                .thenReturn(registeredUser);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Mockito.verify(accessTokenBuilderServiceMock, Mockito.times(2)).build(iamUserInfo);
        Assertions.assertEquals(registeredUser.getUserId(), iamUserInfo.getInnerUserId());
        Assertions.assertEquals(registeredUser.getMappedExternalUserId(), iamUserInfo.getMappedExternalUserId());
    }

    @Test
    void givenExpiredAccessTechnicalTokenWhenPostTokenThenSuccess() {
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        String technicalAccessTokenString = injectTechnicalAccessTokenWithExpirationDate(Instant.now().minus(1, ChronoUnit.HOURS));

        HashMap<String, Claim> expectedClaims = new HashMap<>();
        Mockito.when(validateExternalTokenServiceMock.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedClaims);

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        Mockito.when(idTokenClaimsMapperMock.apply(expectedClaims))
                .thenReturn(iamUserInfo);

        User registeredUser = User.builder().userId("INNERUSERID").mappedExternalUserId("MAPPEDEXTERNALUSERID").build();
        AccessToken technicalAccessToken = AccessToken.builder().accessToken(technicalAccessTokenString).expiresIn(3600).build();
        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(iamUserInfo))
                .thenReturn(technicalAccessToken)
                .thenReturn(expectedAccessToken);

        Mockito.when(iamUserRegistrationServiceMock.registerUser(iamUserInfo, technicalAccessTokenString))
                .thenReturn(registeredUser);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Mockito.verify(accessTokenBuilderServiceMock, Mockito.times(2)).build(iamUserInfo);
        Assertions.assertEquals(registeredUser.getUserId(), iamUserInfo.getInnerUserId());
        Assertions.assertEquals(registeredUser.getMappedExternalUserId(), iamUserInfo.getMappedExternalUserId());
    }

    @Test
    void givenValidTechnicalAccessTokenWhenPostTokenThenSuccess() {
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        String technicalAccessTokenString = injectTechnicalAccessTokenWithExpirationDate(Instant.now().plus(1, ChronoUnit.HOURS));

        HashMap<String, Claim> expectedClaims = new HashMap<>();
        Mockito.when(validateExternalTokenServiceMock.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedClaims);

        IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
        Mockito.when(idTokenClaimsMapperMock.apply(expectedClaims))
                .thenReturn(iamUserInfo);

        User registeredUser = User.builder().userId("INNERUSERID").mappedExternalUserId("MAPPEDEXTERNALUSERID").build();
        AccessToken expectedAccessToken = AccessToken.builder().accessToken("accessToken").build();
        Mockito.when(accessTokenBuilderServiceMock.build(iamUserInfo))
                .thenReturn(expectedAccessToken);

        Mockito.when(iamUserRegistrationServiceMock.registerUser(iamUserInfo, technicalAccessTokenString))
                .thenReturn(registeredUser);

        // When
        AccessToken result = service.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);

        // Then
        Assertions.assertSame(expectedAccessToken, result);
        Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
        Mockito.verify(accessTokenBuilderServiceMock).build(iamUserInfo);
        Assertions.assertEquals(registeredUser.getUserId(), iamUserInfo.getInnerUserId());
        Assertions.assertEquals(registeredUser.getMappedExternalUserId(), iamUserInfo.getMappedExternalUserId());
    }

    private String injectTechnicalAccessTokenWithExpirationDate(Instant expirationInstant) {
        String accessTokenString = "technicalAccessToken";
        ExchangeTokenServiceImpl.CachedTechnicalAccessToken cachedTechnicalAccessToken = new ExchangeTokenServiceImpl.CachedTechnicalAccessToken(
                accessTokenString,
                expirationInstant.atZone(Constants.ZONEID).toOffsetDateTime()
        );
        ReflectionTestUtils.setField(service, "cachedTechnicalAccessToken", cachedTechnicalAccessToken);

        return accessTokenString;
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
