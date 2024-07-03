package it.gov.pagopa.payhub.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.exception.custom.*;
import it.gov.pagopa.payhub.auth.security.JwtAuthenticationFilter;
import it.gov.pagopa.payhub.auth.security.WebSecurityConfig;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthnControllerImpl.class)
@Import({AuthExceptionHandler.class, WebSecurityConfig.class, JwtAuthenticationFilter.class})
class AuthnControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthnService authnServiceMock;

//region desc=postToken tests
    @Test
    void givenExpectedAuthTokenWhenPostTokenThenOk() throws Exception {
        MvcResult result =
                invokePostTokenAndVerify(null, HttpStatus.OK, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("{\"accessToken\":\"token\",\"tokenType\":\"bearer\",\"expiresIn\":0}", result.getResponse().getContentAsString());
    }

    @Test
    void givenRequestWithoutAuthTokenWhenPostTokenThenBadRequest() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/payhub/auth/token")
        ).andExpect(status().isBadRequest()).andReturn();

        AuthErrorDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                AuthErrorDTO.class);
        assertEquals(AuthErrorDTO.ErrorEnum.INVALID_REQUEST, actual.getError());
    }

    @Test
    void givenInvalidExchangeClientExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new InvalidExchangeClientException("description"), HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_CLIENT);
    }

    @Test
    void givenInvalidExchangeRequestExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new InvalidExchangeRequestException("description"), HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @Test
    void givenInvalidGrantTypeExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new InvalidGrantTypeException("description"), HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.UNSUPPORTED_GRANT_TYPE);
    }

    @Test
    void givenInvalidTokenExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new InvalidTokenException("description"), HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_GRANT);
    }

    @Test
    void givenInvalidTokenIssuerExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new InvalidTokenIssuerException("description"), HttpStatus.BAD_REQUEST, AuthErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @Test
    void givenTokenExpiredExceptionWhenPostTokenThenInvalidClientError() throws Exception {
        invokePostTokenAndVerify(new TokenExpiredException("description"), HttpStatus.UNAUTHORIZED, AuthErrorDTO.ErrorEnum.INVALID_GRANT);
    }

    MvcResult invokePostTokenAndVerify(RuntimeException exception, HttpStatus expectedStatus, AuthErrorDTO.ErrorEnum expectedError) throws Exception {
        String clientId = "CLIENT_ID";
        String grantType = "GRANT_TYPE";
        String subjectToken = "SUBJECT_TOKEN";
        String subjectIssuer = "SUBJECT_ISSUER";
        String subjectTokenType = "SUBJECT_TOKEN_TYPE";
        String scope = "SCOPE";

        (exception != null
                ? doThrow(exception)
                : doReturn(new AccessToken("token", "bearer", 0)))
                .when(authnServiceMock).postToken(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);

        MvcResult result = mockMvc.perform(
                post("/payhub/auth/token")
                        .param("client_id", clientId)
                        .param("grant_type", grantType)
                        .param("subject_token", subjectToken)
                        .param("subject_issuer", subjectIssuer)
                        .param("subject_token_type", subjectTokenType)
                        .param("scope", scope)
        ).andExpect(status().is(expectedStatus.value())).andReturn();

        if (exception != null && expectedError != null) {
            AuthErrorDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                    AuthErrorDTO.class);
            assertEquals(expectedError, actual.getError());
            assertEquals(exception.getMessage(), actual.getErrorDescription());
        } else {
            Assertions.assertFalse(result.getResponse().getContentAsString().contains("error"));
        }

        return result;
    }
//endregion

//region desc=getUserInfo tests
    @Test
    void givenRequestWithoutAuthorizationWhenGetUserInfoThenForbidden() throws Exception {
        mockMvc.perform(
                get("/payhub/auth/userinfo")
        ).andExpect(status().isForbidden());
    }

    @Test
    void givenRequestWitAuthorizationWhenGetUserInfoThenOk() throws Exception {
        UserInfo expectedUser = UserInfo.builder()
                .userId("USERID")
                .organizationAccess("IPA_CODE")
                .organizations(List.of(UserOrganizationRoles.builder()
                                .organizationIpaCode("IPA_CODE")
                                .roles(List.of("ROLE"))
                        .build()))
                .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(expectedUser);

        mockMvc.perform(
                        get("/payhub/auth/userinfo")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andExpect(content().json("{\"userId\":\"USERID\"}"));
    }

    @Test
    void givenRequestWitAuthorizationAndNotOrganizationAccessWhenGetUserInfoThenOk() throws Exception {
        UserInfo expectedUser = UserInfo.builder().userId("USERID").build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(expectedUser);

        mockMvc.perform(
                        get("/payhub/auth/userinfo")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andExpect(content().json("{\"userId\":\"USERID\"}"));
    }

    @Test
    void givenRequestWithInvalidAuthorizationWhenGetUserInfoThenForbidden() throws Exception {
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenThrow(new InvalidAccessTokenException(""));

        mockMvc.perform(
                get("/payhub/auth/userinfo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }

    @Test
    void givenRequestWithUserNotFoundWhenGetUserInfoThenForbidden() throws Exception {
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenThrow(new UserNotFoundException(""));

        mockMvc.perform(
                get("/payhub/auth/userinfo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }
//endregion

//region desc=logout tests
    @Test
    void givenNoClientIdWhenLogoutThenBadRequest() throws Exception {
        mockMvc.perform(
                post("/payhub/auth/revoke")
                        .param("token", "token")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidClientIdWhenLogoutThenBadRequest() throws Exception {
        mockMvc.perform(
                post("/payhub/auth/revoke")
                        .param("token", "token")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void givenCompleteRequestWhenLogoutThenInvalidClientError() throws Exception {
        String clientId = "CLIENTID";
        String token = "TOKEN";

        Mockito.doThrow(new InvalidExchangeClientException(""))
                .when(authnServiceMock).logout(clientId, token);

        MvcResult result = mockMvc.perform(
                post("/payhub/auth/revoke")
                        .param("client_id", clientId)
                        .param("token", token)
        ).andExpect(status().isUnauthorized()).andReturn();

        AuthErrorDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                AuthErrorDTO.class);
        assertEquals(AuthErrorDTO.ErrorEnum.INVALID_CLIENT, actual.getError());
        assertEquals("", actual.getErrorDescription());
    }
//end region
}
