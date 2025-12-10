package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidGrantTypeException;
import it.gov.pagopa.payhub.auth.service.exchange.ExchangeTokenService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import it.gov.pagopa.payhub.auth.service.logout.LogoutService;
import it.gov.pagopa.payhub.auth.service.m2m.ClientCredentialService;
import it.gov.pagopa.payhub.auth.service.m2m.ValidateClientCredentialsService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthnServiceTest {

    @Mock
    private ClientCredentialService clientCredentialService;
    @Mock
    private ExchangeTokenService exchangeTokenServiceMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private LogoutService logoutServiceMock;
    @Mock
    private AuditLoggerService auditLoggerServiceMock;
    @Mock
    private LimitedTokenService limitedTokenServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;

    private AuthnService service;

    @BeforeEach
    void init(){
        service = new AuthnServiceImpl(
                clientCredentialService,
                exchangeTokenServiceMock,
                userServiceMock,
                logoutServiceMock,
                auditLoggerServiceMock,
                limitedTokenServiceMock,
                organizationServiceMock
        );
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
          clientCredentialService,
          exchangeTokenServiceMock,
          userServiceMock,
          logoutServiceMock
        );
    }

    @Test
    void whenPostTokenThenCallExchangeService(){
        // Given
        String accessToken = "eyJraWQiOiIzNTMxNTA2Ny05YmVjLTMxY2MtYmIwMi0zMzBhZTZlOGY0NjIiLCJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI1OWEyNWU5NC01MDFjLTQxNDEtODdiZS1hYzdjNWE2YTExZDkiLCJzdWIiOiJFVXFLaUQxcHNMckdOdUx4Q0d6cml5LXJveVBsQnZ1eWVKTWMwZHhheE5zPSIsImlhdCI6MTc2NTM2MTQxMiwiZXhwIjoxNzY1Mzc1ODEyLCJvcmdhbml6YXRpb25JcGFDb2RlIjoiREVNTyJ9.X9wYsGzDtC8H3-QnlcUzMgd_2VAPJv8K10rqyBDeYOLBNWl0OdhjXWi6sCIdwjqTmS9pLMA9AHfzDPDUSJro1am2HPvrZe1hJ7XycapqqHsCXvtmeRRwE6c-WZ5bxxsxEB3-PSRvia_6C3c7x58QI18DzAl57u63jgE9rHhDQp-moXaAQ7c5obs2Kp2WnO_eh9gBs5CP2bBBWIZaFBwROrEmwSNuOCmkg-IpfVCQ1yEwzBNl9P1IKG4a31vN64zxNS8cFAlwrN3tmkFp1e4kwWOxY9VBLloH8f-VWr_PGLSlEQS_P1M3rI4u5g6Luk3gKGE_M2xSOJhjq-twXWr4PA";
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
				String clientSecret = "CLIENT_SECRET";
        Organization organization = new Organization();
        organization.setOrganizationId(1L);
        organization.setOrgName("orgName");

        String grantType= ValidateExternalTokenService.ALLOWED_GRANT_TYPE;
        AccessToken expectedResult = new AccessToken().accessToken(accessToken);
        Mockito.when(exchangeTokenServiceMock.postToken(clientId, subjectToken, subjectIssuer, subjectTokenType, scope))
                .thenReturn(expectedResult);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("DEMO", accessToken)).thenReturn(organization);

        // When
        AccessToken result = service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        // Then
        Map<String, String> label2value = Map.ofEntries(
                Map.entry("grantType", grantType),
                Map.entry("organizationId", String.valueOf(organization.getOrganizationId())),
                Map.entry("organizationName", organization.getOrgName())
        );
        Mockito.verify(auditLoggerServiceMock).log(AuditEventType.LOGIN_SUCCESS, label2value,"Authentication success" );
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenPostTokenThenCallClientCredentialService(){
        // Given
        String accessToken = "eyJraWQiOiIzNTMxNTA2Ny05YmVjLTMxY2MtYmIwMi0zMzBhZTZlOGY0NjIiLCJ0eXAiOiJhdCtKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJiZWFyZXIiLCJpc3MiOiJkZXYucGlhdHRhZm9ybWF1bml0YXJpYS5wYWdvcGEuaXQiLCJqdGkiOiI1OWEyNWU5NC01MDFjLTQxNDEtODdiZS1hYzdjNWE2YTExZDkiLCJzdWIiOiJFVXFLaUQxcHNMckdOdUx4Q0d6cml5LXJveVBsQnZ1eWVKTWMwZHhheE5zPSIsImlhdCI6MTc2NTM2MTQxMiwiZXhwIjoxNzY1Mzc1ODEyLCJvcmdhbml6YXRpb25JcGFDb2RlIjoiREVNTyJ9.X9wYsGzDtC8H3-QnlcUzMgd_2VAPJv8K10rqyBDeYOLBNWl0OdhjXWi6sCIdwjqTmS9pLMA9AHfzDPDUSJro1am2HPvrZe1hJ7XycapqqHsCXvtmeRRwE6c-WZ5bxxsxEB3-PSRvia_6C3c7x58QI18DzAl57u63jgE9rHhDQp-moXaAQ7c5obs2Kp2WnO_eh9gBs5CP2bBBWIZaFBwROrEmwSNuOCmkg-IpfVCQ1yEwzBNl9P1IKG4a31vN64zxNS8cFAlwrN3tmkFp1e4kwWOxY9VBLloH8f-VWr_PGLSlEQS_P1M3rI4u5g6Luk3gKGE_M2xSOJhjq-twXWr4PA";
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
        String clientSecret = "CLIENT_SECRET";
        Organization organization = new Organization();
        organization.setOrganizationId(1L);
        organization.setOrgName("orgName");

        String grantType= ValidateClientCredentialsService.ALLOWED_GRANT_TYPE;
        AccessToken expectedResult = new AccessToken().accessToken(accessToken);
        Mockito.when(clientCredentialService.postToken(clientId, scope, clientSecret)).thenReturn(expectedResult);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("DEMO", accessToken)).thenReturn(organization);

        // When
        AccessToken result = service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        // Then
        Map<String, String> label2value = Map.ofEntries(
                Map.entry("grantType", grantType),
                Map.entry("organizationId", String.valueOf(organization.getOrganizationId())),
                Map.entry("organizationName", organization.getOrgName())
        );
        Mockito.verify(auditLoggerServiceMock).log(AuditEventType.LOGIN_SUCCESS, label2value,"Authentication success" );
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenPostTokenWhenCallClientCredentialServiceThenInvalidGrantTypeException(){
        // Given
        String clientId="CLIENT_ID";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";
        String clientSecret = "CLIENT_SECRET";

        String grantType="UNEXPECTED_GRANT_TYPE";
        // When, Then
        assertThrows(InvalidGrantTypeException.class, () ->
            service.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret));
    }

    @Test
    void whenGetUserInfoThenCallUserService(){
        // Given
        String accessToken = "accessToken";
        UserInfo expectedResult = new UserInfo();
        Mockito.when(userServiceMock.getUserInfo(accessToken))
                .thenReturn(expectedResult);

        // When
        UserInfo result = service.getUserInfo(accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenLogoutThenCallLogout(){
        // Given
        String clientId = "clientId";
        String accessToken = "accessToken";

        // When
        service.logout(clientId, accessToken);

        // Then
        Mockito.verify(logoutServiceMock).logout(clientId, accessToken);
    }

    @Test
    void whenPostLimitedTokenThenCallLimitedTokenService(){
        // Given
        LimitedTokenRequest request = LimitedTokenRequest.builder()
                .resource("resource")
                .app("app")
                .resourceId("resourceid")
                .expireInSeconds(3600L)
                .organizationId(1L)
                .singleUsage(false)
                .build();

        AccessToken expectedResult = AccessToken.builder()
                        .accessToken("abc")
                        .tokenType("typ")
                        .expiresIn(3600)
                        .build();

        Mockito.when(limitedTokenServiceMock.generate(request))
                .thenReturn(expectedResult);

        // When
        AccessToken result = service.postLimitedToken(request);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
