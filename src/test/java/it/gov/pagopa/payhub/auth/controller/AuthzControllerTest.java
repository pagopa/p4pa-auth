package it.gov.pagopa.payhub.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.exception.custom.M2MClientConflictException;
import it.gov.pagopa.payhub.auth.exception.custom.OperatorNotFoundException;
import it.gov.pagopa.payhub.auth.security.JwtAuthenticationFilter;
import it.gov.pagopa.payhub.auth.security.WebSecurityConfig;
import it.gov.pagopa.payhub.auth.service.*;
import it.gov.pagopa.payhub.auth.service.m2m.legacy.JWTLegacyHandlerService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthzControllerImpl.class)
@Import({AuthExceptionHandler.class, WebSecurityConfig.class, JwtAuthenticationFilter.class})
class AuthzControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthzService authzServiceMock;

    @MockitoBean
    private AuthnService authnServiceMock;

    @MockitoBean
    private AuditLoggerService auditLoggerServiceMock;

    @MockitoBean
    private ValidateTokenService validateTokenServiceMock;

    @MockitoBean
    private JWTLegacyHandlerService jwtLegacyHandlerServiceMock;

    @MockitoBean
    private AccessTokenBuilderService accessTokenBuilderServiceMock;

    //region desc=getOrganizationOperators tests
    @Test
    void givenAuthorizedUserwhenGetOrganizationOperatorsThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(4, 1);

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(UserInfo.builder()
                        .organizations(List.of(UserOrganizationRoles.builder()
                                .organizationIpaCode(organizationIpaCode)
                                .roles(List.of(Constants.ROLE_ADMIN))
                                .build()))
                        .build());

        Page<OperatorDTO> expectedResult = new PageImpl<>(
                List.of(OperatorDTO.builder()
                        .userId("USER1")
                        .build()),
                pageRequest,
                100
        );
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.getOrganizationOperators(organizationIpaCode, pageRequest))
                .thenReturn(expectedResult);

        mockMvc.perform(
                        get("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                                .param("page", "4")
                                .param("size", "1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"userId\":\"USER1\"}],\"pageNo\":4,\"pageSize\":1,\"totalElements\":100,\"totalPages\":100}"));
    }

    @Test
    void givenAuthorizedUserWhenGetOrganizationOperatorsWithQueryParamsThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(4, 1);
        String fiscalCode = "FISCALCODE";
        String firstName = "FIRSTNAME";
        String lastName = "LASTNAME";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        Page<OperatorDTO> expectedResult = new PageImpl<>(
            List.of(OperatorDTO.builder()
                .userId("USER1")
                .fiscalCode(fiscalCode)
                    .firstName(firstName)
                    .lastName(lastName)
                .build()),
            pageRequest,
            100
        );
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.getOrganizationOperators(organizationIpaCode, fiscalCode,
                firstName, lastName, pageRequest))
            .thenReturn(expectedResult);

        mockMvc.perform(
                get("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                    .param("fiscalCode", fiscalCode)
                    .param("firstName", firstName)
                    .param("lastName", lastName)
                    .param("page", "4")
                    .param("size", "1")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk())
            .andExpect(content().json("{\"content\":[{\"userId\":\"USER1\"}],\"pageNo\":4,\"pageSize\":1,\"totalElements\":100,\"totalPages\":100}"));
    }

    @Test
    void givenUnauthorizedUserwhenGetOrganizationOperatorsThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(UserInfo.builder()
                        .organizations(List.of(UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG2")
                                .roles(List.of(Constants.ROLE_ADMIN))
                                .build()))
                        .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
                        get("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                                .param("page", "4")
                                .param("size", "1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isForbidden());
    }
    //end region
    //region desc=getOrganizationOperator tests
    @Test
    void givenAuthorizedUserWhenGetOrganizationOperatorThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        OperatorDTO expectedResult = OperatorDTO.builder()
                .userId(mappedExternalUserId+organizationIpaCode)
                .mappedExternalUserId(mappedExternalUserId)
                .organizationIpaCode(organizationIpaCode)
                .build();

        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.getOrganizationOperator(organizationIpaCode, mappedExternalUserId)).thenReturn(expectedResult);

        mockMvc.perform(
                get("/payhub/am/operators/{organizationIpaCode}/{mappedExternalUserId}", organizationIpaCode, mappedExternalUserId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk());
    }

    @Test
    void givenOperatorNotFoundWhenGetOrganizationOperatorThenException() throws Exception {
        String organizationIpaCode = "IPACODE";
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        OperatorNotFoundException exception = new OperatorNotFoundException("");

        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.getOrganizationOperator(organizationIpaCode, mappedExternalUserId)).thenThrow(exception);

        mockMvc.perform(
            get("/payhub/am/operators/{organizationIpaCode}/{mappedExternalUserId}", organizationIpaCode, mappedExternalUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isNotFound())
            .andExpect(result -> Assertions.assertInstanceOf(OperatorNotFoundException.class,
                result.getResolvedException()));
    }

    //end region
    //region desc=createOrganizationOperator tests
    @Test
    void givenIsNotImplementedWhenCreateOrganizationOperatorThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        String body = AuthzControllerNoOrganizationAccessModeTest.buildCreateOperatorRequest();
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
            post("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isNotImplemented());
    }
    //end region
    //region createUser
    @Test
    void givenIsNotImplementedWhenCreateUserThenError() throws Exception {
        String body = AuthzControllerNoOrganizationAccessModeTest.buildCreateUserRequest();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
            post("/payhub/am/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isNotImplemented());
    }
    //end region
    //region getUserInfoFromMappedExternalUserId
    @Test
    void givenLoggedUserRequestWhenGetUserInfoThenOk() throws Exception {
        //Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        String accessToken = "accessToken";
        UserInfo expectedUser = UserInfo.builder()
            .userId("USERID")
            .organizationAccess("IPA_CODE")
            .mappedExternalUserId(mappedExternalUserId)
            .organizations(List.of(UserOrganizationRoles.builder()
                .organizationIpaCode("IPA_CODE")
                .roles(List.of("ROLE_OPER"))
                .build()))
            .build();

        Mockito.when(authnServiceMock.getUserInfo(accessToken))
            .thenReturn(UserInfo.builder()
                .userId("USERID")
                .mappedExternalUserId(mappedExternalUserId)
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn(accessToken);

        //When
        Mockito.when(authzServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken))
            .thenReturn(expectedUser);

        //Then
        mockMvc.perform(
                get("/payhub/oauth/userinfo/{mappedExternalUserId}", mappedExternalUserId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk())
            .andExpect(content().json("{\"userId\":\"USERID\",\"mappedExternalUserId\":\"MAPPEDEXTERNALUSERID\",\"organizations\":[{\"organizationIpaCode\":\"ORG\",\"roles\":[\"ROLE_ADMIN\"]}]}"));
    }

    @Test
    void givenOtherUserAndAdminLoggedinWhenGetUserInfoThenOk() throws Exception {
        //Given
        String mappedExternalUserId = "OTHERUSERID";
        String accessToken = "accessToken";
        UserInfo expectedUser = UserInfo.builder()
                .userId("USERID")
                .organizationAccess("IPA_CODE")
                .mappedExternalUserId(mappedExternalUserId)
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationIpaCode("IPA_CODE")
                        .roles(List.of("ROLE_OPER"))
                        .build()))
                .build();

        Mockito.when(authnServiceMock.getUserInfo(accessToken))
                .thenReturn(UserInfo.builder()
                        .mappedExternalUserId("LOGGEDUSERID")
                        .organizations(List.of(UserOrganizationRoles.builder()
                                .organizationIpaCode("ORG")
                                .roles(List.of(Constants.ROLE_ADMIN))
                                .build()))
                        .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn(accessToken);

        //When
        Mockito.when(authzServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken))
                .thenReturn(expectedUser);

        //Then
        mockMvc.perform(
                        get("/payhub/oauth/userinfo/{mappedExternalUserId}", mappedExternalUserId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andExpect(content().json("{\"userId\":\"USERID\",\"mappedExternalUserId\":\"OTHERUSERID\"}"));
    }

    @Test
    void givenRequestUnauthorizedWhenGetUserInfoThenException() throws Exception {
        //Given
        String mappedExternalUserId = "OTHEREXTERNALUSERID";
        String accessToken = "accessToken";
        UserInfo expectedUser = UserInfo.builder()
            .userId("USERID")
            .organizationAccess("IPA_CODE")
            .mappedExternalUserId(mappedExternalUserId)
            .organizations(List.of(UserOrganizationRoles.builder()
                .organizationIpaCode("IPA_CODE")
                .roles(List.of("ROLE"))
                .build()))
            .build();

        Mockito.when(authnServiceMock.getUserInfo(accessToken))
            .thenReturn(UserInfo.builder()
                .mappedExternalUserId("EXTERNALUSERID")
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG")
                    .roles(List.of(Constants.ROLE_OPER))
                    .build()))
                .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn(accessToken);

        //When
        Mockito.when(authzServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken))
            .thenReturn(expectedUser);

        //Then
        mockMvc.perform(
                get("/payhub/oauth/userinfo/{mappedExternalUserId}", mappedExternalUserId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isForbidden());
    }
    //end region

//region getClient tests
@Test
void givenAuthorizedUserWhenGetClientThenOk() throws Exception {
    String organizationIpaCode = "IPA_TEST_2";
    String clientId = "CLIENTID";
    String clientName = "Test Client";
    String decryptedSecret = "decryptedSecret";

    ClientDTO expectedClientDTO = ClientDTO.builder()
            .clientId(clientId)
            .clientName(clientName)
            .organizationIpaCode(organizationIpaCode)
            .clientSecret(decryptedSecret)
            .build();

    UserInfo expectedUser = UserInfo.builder()
            .userId("USERID")
            .organizationAccess(organizationIpaCode)
            .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
            .build();

    Mockito.when(authnServiceMock.getUserInfo("accessToken")).thenReturn(expectedUser);
    Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
    Mockito.when(authzServiceMock.getClient(organizationIpaCode, clientId))
            .thenReturn(Optional.of(expectedClientDTO));

    MvcResult result = mockMvc.perform(
                    get("/payhub/oauth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk())
            .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    ClientDTO actualClientDTO = objectMapper.readValue(jsonResponse, ClientDTO.class);

    assertEquals(expectedClientDTO.getClientId(), actualClientDTO.getClientId());
    assertEquals(expectedClientDTO.getClientName(), actualClientDTO.getClientName());
    assertEquals(expectedClientDTO.getOrganizationIpaCode(), actualClientDTO.getOrganizationIpaCode());
    assertEquals(expectedClientDTO.getClientSecret(), actualClientDTO.getClientSecret());
}

    @Test
    void givenRequestUnauthorizedWhenGetClientThenException() throws Exception {
        String organizationIpaCode = "IPA_TEST_2";
        String clientId = "CLIENTID";

        UserInfo unauthorizedUser = UserInfo.builder()
                .userId("USERID")
                .organizationAccess("ORG")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationIpaCode("ORG")
                        .roles(List.of(Constants.ROLE_OPER))
                        .build()))
                .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(unauthorizedUser);
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix())
                .thenReturn("accessToken");

        mockMvc.perform(
                get("/payhub/oauth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }
//end region

//region getClients tests
    @Test
    void givenAuthorizedUserWhenGetClientsThenOk() throws Exception {
        //Given
        String organizationIpaCode = "IPA_TEST_2";
        String clientName1 = "SERVICE_001";
        String clientName2 = "SERVICE_002";
        UserInfo expectedUser = UserInfo.builder()
          .userId("USERID")
          .organizationAccess(organizationIpaCode)
          .organizations(List.of(UserOrganizationRoles.builder()
            .organizationIpaCode(organizationIpaCode)
            .roles(List.of(Constants.ROLE_ADMIN))
            .build()))
          .build();

        ClientNoSecretDTO dto1 = ClientNoSecretDTO.builder()
          .organizationIpaCode(organizationIpaCode)
          .clientName(clientName1)
          .clientId(organizationIpaCode + clientName1)
          .build();
        ClientNoSecretDTO dto2 = ClientNoSecretDTO.builder()
          .organizationIpaCode(organizationIpaCode)
          .clientName(clientName2)
          .clientId(organizationIpaCode + clientName2)
          .build();
        List<ClientNoSecretDTO> expectedDTOList = List.of(dto1, dto2);
        //When
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(expectedUser);
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        doReturn(expectedDTOList)
          .when(authzServiceMock).getClients(organizationIpaCode);
        //Then
        MvcResult result = mockMvc.perform(
            get("/payhub/oauth/clients/{organizationIpaCode}", organizationIpaCode)
              .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
          ).andExpect(status().isOk())
          .andReturn();

        List<ClientNoSecretDTO> responseList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ArrayList<ClientNoSecretDTO>>(){});

        assertEquals(expectedDTOList, responseList);
    }

    @Test
    void givenRequestUnauthorizedWhenGetClientsThenException() throws Exception {
        //Given
        String organizationIpaCode = "IPA_TEST_2";

        //When
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(UserInfo.builder()
            .organizations(List.of(UserOrganizationRoles.builder()
              .organizationIpaCode("ORG")
              .roles(List.of(Constants.ROLE_OPER))
              .build()))
            .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        //Then
        mockMvc.perform(
          get("/payhub/oauth/clients/{organizationIpaCode}", organizationIpaCode)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }
//end region

//region
    @Test
    void givenAlreadyExistentClientWhenRegisterClientThenConflict() throws Exception {
        String organizationIpaCode = "IPACODE";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
                .thenReturn(UserInfo.builder()
                        .organizations(List.of(UserOrganizationRoles.builder()
                                .organizationIpaCode(organizationIpaCode)
                                .roles(List.of(Constants.ROLE_ADMIN))
                                .build()))
                        .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        CreateClientRequest createClientRequest = new CreateClientRequest();
        createClientRequest.setClientName("CLIENTNAME");
        Mockito.when(authzServiceMock.registerClient(organizationIpaCode, createClientRequest))
                .thenThrow(new M2MClientConflictException(""));

        mockMvc.perform(
                post("/payhub/oauth/clients/{organizationIpaCode}", organizationIpaCode)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientRequest))
        ).andExpect(status().isConflict());
    }
//end region

//region revokeClient tests
    @Test
    void givenAuthorizedUserWhenRevokeClientThenOk() throws Exception {
        String organizationIpaCode = "IPA_TEST_2";
        String clientId = "CLIENTID";

        UserInfo expectedUser = UserInfo.builder()
          .userId("USERID")
          .organizationAccess(organizationIpaCode)
          .organizations(List.of(UserOrganizationRoles.builder()
            .organizationIpaCode(organizationIpaCode)
            .roles(List.of(Constants.ROLE_ADMIN))
            .build()))
          .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(expectedUser);
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        willDoNothing().given(authzServiceMock).revokeClient(organizationIpaCode, clientId);

        mockMvc.perform(
            delete("/payhub/oauth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
              .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
          ).andExpect(status().isOk())
          .andDo(print());
    }

    @Test
    void givenUnauthorizedUserWhenRevokeClientThenException() throws Exception {
        //Given
        String organizationIpaCode = "IPA_TEST_2";
        String clientId = "CLIENTID";

        //When
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(UserInfo.builder()
            .organizations(List.of(UserOrganizationRoles.builder()
              .organizationIpaCode("ORG")
              .roles(List.of(Constants.ROLE_OPER))
              .build()))
            .build());
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        //Then
        mockMvc.perform(
          delete("/payhub/oauth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }
    //end region

    @Test
    void givenAuthorizedUserWhenGetClientByFiltersThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";

        Pageable pageRequest = PageRequest.of(4, 1);

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        Page<ClientNoSecretDTO> expectedResult = new PageImpl<>(
            List.of(ClientNoSecretDTO.builder()
                .organizationIpaCode(organizationIpaCode)
                .build()),
            pageRequest,
            100
        );
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.getClientsSearch(null, null, organizationIpaCode, pageRequest))
            .thenReturn(expectedResult);

        mockMvc.perform(
                get("/payhub/oauth/clients/search")
                    .param("organizationIpaCode","IPACODE")
                    .param("page", "4")
                    .param("size", "1")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk())
            .andExpect(content().json("{\"content\":[{\"organizationIpaCode\":\"IPACODE\"}],\"pageNo\":4,\"pageSize\":1,\"totalElements\":100,\"totalPages\":100}"));
    }

    @Test
    void givenUnauthorizedUserwhenGetClientByFiltersThenUnauthorized() throws Exception {
        String organizationIpaCode = "IPACODE";

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_OPER))
                    .build()))
                .build());

        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
                get("/payhub/oauth/clients/search")
                    .param("organizationIpaCode","IPACODE")
                    .param("page", "4")
                    .param("size", "1")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isForbidden());
    }

    @Test
    void givenAuthorizedUserWhenGenerateClientSecretThenReturnOk() throws Exception {
        String organizationIpaCode = "IPA_TEST_2";
        String clientId = "CLIENTID";
        String clientName = "Test Client";
        String generatedSecret = "newGeneratedSecret";

        ClientDTO expectedClientDTO = ClientDTO.builder()
                .clientId(clientId)
                .clientName(clientName)
                .organizationIpaCode(organizationIpaCode)
                .clientSecret(generatedSecret)
                .build();

        UserInfo authorizedUser = UserInfo.builder()
                .userId("USERID")
                .organizationAccess(organizationIpaCode)
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationIpaCode(organizationIpaCode)
                        .roles(List.of(Constants.ROLE_ADMIN))
                        .build()))
                .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken")).thenReturn(authorizedUser);
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        Mockito.when(authzServiceMock.generateClientSecret(organizationIpaCode, clientId))
                .thenReturn(Optional.of(expectedClientDTO));

        MvcResult result = mockMvc.perform(
                        post("/payhub/oauth/clients/{organizationIpaCode}/{clientId}/generate", organizationIpaCode, clientId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ClientDTO actualClientDTO = objectMapper.readValue(jsonResponse, ClientDTO.class);

        assertEquals(expectedClientDTO.getClientId(), actualClientDTO.getClientId());
        assertEquals(expectedClientDTO.getClientName(), actualClientDTO.getClientName());
        assertEquals(expectedClientDTO.getOrganizationIpaCode(), actualClientDTO.getOrganizationIpaCode());
        assertEquals(expectedClientDTO.getClientSecret(), actualClientDTO.getClientSecret());
    }

    @Test
    void givenUnauthorizedUserWhenGenerateClientSecretThenReturnUnauthorized() throws Exception {
        String organizationIpaCode = "IPA_TEST_2";
        String clientId = "CLIENTID";

        UserInfo unauthorizedUser = UserInfo.builder()
                .userId("USERID")
                .organizationAccess("ORG")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationIpaCode("ORG")
                        .roles(List.of(Constants.ROLE_OPER))
                        .build()))
                .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken")).thenReturn(unauthorizedUser);
        Mockito.when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
                post("/payhub/oauth/clients/{organizationIpaCode}/{clientId}/generate", organizationIpaCode, clientId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isForbidden());
    }
}
