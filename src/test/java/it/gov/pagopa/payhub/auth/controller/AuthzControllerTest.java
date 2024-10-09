package it.gov.pagopa.payhub.auth.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.exception.custom.ClientNotFoundException;
import it.gov.pagopa.payhub.auth.exception.custom.OperatorNotFoundException;
import it.gov.pagopa.payhub.auth.security.JwtAuthenticationFilter;
import it.gov.pagopa.payhub.auth.security.WebSecurityConfig;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthzControllerImpl.class)
@Import({AuthExceptionHandler.class, WebSecurityConfig.class, JwtAuthenticationFilter.class})
class AuthzControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthzService authzServiceMock;

    @MockBean
    private AuthnService authnServiceMock;

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

        Mockito.when(authzServiceMock.getOrganizationOperators(organizationIpaCode, pageRequest))
                .thenReturn(expectedResult);

        mockMvc.perform(
                        get("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                                .param("page", "4")
                                .param("size", "1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"userId\":\"USER1\"}],\"pageNo\":4,\"pageSize\":1,\"totalElements\":1,\"totalPages\":100}"));
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
            .andExpect(content().json("{\"content\":[{\"userId\":\"USER1\"}],\"pageNo\":4,\"pageSize\":1,\"totalElements\":1,\"totalPages\":100}"));
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

        mockMvc.perform(
                        get("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                                .param("page", "4")
                                .param("size", "1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isUnauthorized());
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
        CreateOperatorRequest request = new CreateOperatorRequest();
        request.setExternalUserId("EXTERNALUSERID");
        Gson gson = new Gson();
        String body = gson.toJson(request);
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

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
        UserDTO request = new UserDTO();
        request.setExternalUserId("EXTERNALUSERID");
        Gson gson = new Gson();
        String body = gson.toJson(request);
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

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
    void givenRequestWitAuthorizationWhenGetUserInfoThenOk() throws Exception {
        //Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        UserInfo expectedUser = UserInfo.builder()
            .userId("USERID")
            .organizationAccess("IPA_CODE")
            .mappedExternalUserId(mappedExternalUserId)
            .organizations(List.of(UserOrganizationRoles.builder()
                .organizationIpaCode("IPA_CODE")
                .roles(List.of("ROLE_OPER"))
                .build()))
            .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        //When
        Mockito.when(authzServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId))
            .thenReturn(expectedUser);

        //Then
        mockMvc.perform(
                get("/payhub/auth/userinfo/{mappedExternalUserId}", mappedExternalUserId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isOk())
            .andExpect(content().json("{\"userId\":\"USERID\"}"));
    }

    @Test
    void givenRequestUnauthorizedWhenGetUserInfoThenException() throws Exception {
        //Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        UserInfo expectedUser = UserInfo.builder()
            .userId("USERID")
            .organizationAccess("IPA_CODE")
            .mappedExternalUserId(mappedExternalUserId)
            .organizations(List.of(UserOrganizationRoles.builder()
                .organizationIpaCode("IPA_CODE")
                .roles(List.of("ROLE"))
                .build()))
            .build();

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG")
                    .roles(List.of(Constants.ROLE_OPER))
                    .build()))
                .build());

        //When
        Mockito.when(authzServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId))
            .thenReturn(expectedUser);

        //Then
        mockMvc.perform(
                get("/payhub/auth/userinfo/{mappedExternalUserId}", mappedExternalUserId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            ).andExpect(status().isUnauthorized());
    }
    //end region

    @Test
    void givenAuthorizedUserWhenGetClientSecretThenOk() throws Exception {
        String uuidRandomForClientSecret = UUID.randomUUID().toString();
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

        doReturn(uuidRandomForClientSecret)
          .when(authzServiceMock).getClientSecret(organizationIpaCode, clientId);

        MvcResult result = mockMvc.perform(
            get("/payhub/auth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
              .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
          ).andExpect(status().isOk())
          .andReturn();

        assertEquals(uuidRandomForClientSecret, result.getResponse().getContentAsString());
    }

    @Test
    void givenRequestUnauthorizedWhenGetClientSecretThenException() throws Exception {
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

        //Then
        mockMvc.perform(
          get("/payhub/auth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isUnauthorized());
    }

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

        ClientDTO dto1 = ClientDTO.builder()
          .organizationIpaCode(organizationIpaCode)
          .clientName(clientName1)
          .clientId(organizationIpaCode + clientName1)
          .clientSecret(UUID.randomUUID().toString())
          .build();
        ClientDTO dto2 = ClientDTO.builder()
          .organizationIpaCode(organizationIpaCode)
          .clientName(clientName2)
          .clientId(organizationIpaCode + clientName2)
          .clientSecret(UUID.randomUUID().toString())
          .build();
        List<ClientDTO> clientDTOList = List.of(dto1, dto2);
        //When
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(expectedUser);

        doReturn(clientDTOList)
          .when(authzServiceMock).getClients(organizationIpaCode);
        //Then
        MvcResult result = mockMvc.perform(
            get("/payhub/auth/clients/{organizationIpaCode}", organizationIpaCode)
              .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
          ).andExpect(status().isOk())
          .andReturn();

        List<ClientDTO> responseList = new Gson()
          .fromJson(result.getResponse().getContentAsString(), new TypeToken<ArrayList<ClientDTO>>(){}.getType());

        assertEquals(clientDTOList, responseList);
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

        //Then
        mockMvc.perform(
          get("/payhub/auth/clients/{organizationIpaCode}", organizationIpaCode)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
        ).andExpect(status().isUnauthorized());
    }

}
