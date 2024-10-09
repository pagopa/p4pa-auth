package it.gov.pagopa.payhub.auth.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.security.JwtAuthenticationFilter;
import it.gov.pagopa.payhub.auth.security.WebSecurityConfig;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.auth.service.ValidateTokenService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthzControllerImpl.class)
@Import({AuthExceptionHandler.class, WebSecurityConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = { "app.enable-access-organization-mode=false" })
class AuthzControllerNoOrganizzationAccessModeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthzService authzServiceMock;

    @MockBean
    private AuthnService authnServiceMock;

    @MockBean
    private ValidateTokenService validateTokenServiceMock;

// createOperator region
    @Test
    void givenUnauthorizedUserWhenCreateOrganizationOperatorThenOk() throws Exception {
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
                .content(String.valueOf((body)))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthorizedUserWhenCreateOrganizationOperatorThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        CreateOperatorRequest createOperatorRequest = new CreateOperatorRequest();
        createOperatorRequest.setExternalUserId("externalUserId");
        createOperatorRequest.setFiscalCode("fiscalCode");
        createOperatorRequest.setFirstName("firstName");
        createOperatorRequest.setLastName("lastName");
        createOperatorRequest.setEmail("email@example.com");
        createOperatorRequest.setRoles(List.of("ROLE_ADMIN"));
        Gson gson = new Gson();
        String body = gson.toJson(createOperatorRequest);


        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        mockMvc.perform(
            post("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk());
    }
    // end region
    //createUser region
    @Test
    void givenAuthorizedUserWhenCreateUserThenOk() throws Exception {
        UserDTO user = new UserDTO();
        user.setExternalUserId("EXTERNALUSERID");
        user.setFiscalCode("FISCALCODE");
        user.setFirstName("FIRSTNAME");
        user.setLastName("LASTNAME");
        Gson gson = new Gson();
        String body = gson.toJson(user);

        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("IPA_TEST_2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());

        mockMvc.perform(
            post("/payhub/am/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk());
    }

    @Test
    void givenUnauthorizedUserWhenCreateUserThenOk() throws Exception {
        UserDTO request = new UserDTO();
        request.setExternalUserId("EXTERNALUSERID");
        Gson gson = new Gson();
        String body = gson.toJson(request);
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("IPA_TEST_2")
                    .roles(List.of(Constants.ROLE_OPER))
                    .build()))
                .build());
        mockMvc.perform(
            post("/payhub/am/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf((body)))
        ).andExpect(status().isUnauthorized());
    }
    //end region

    @Test
    void givenUnauthorizedUserWhenRegisterClientThenUnauthorizedException() throws Exception {
        String organizationIpaCode = "IPACODE";
        CreateClientRequest request = new CreateClientRequest();
        request.setClientName("CLIENTNAME");
        Gson gson = new Gson();
        String body = gson.toJson(request);
        Mockito.when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(UserInfo.builder()
            .organizations(List.of(UserOrganizationRoles.builder()
              .organizationIpaCode("ORG2")
              .roles(List.of(Constants.ROLE_OPER))
              .build()))
            .build());
        mockMvc.perform(
          post("/payhub/auth/clients/{organizationIpaCode}", organizationIpaCode)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf((body)))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthorizedUserWhenRegisterClientThenOk() throws Exception {
        String uuidRandomForSecret = UUID.randomUUID().toString();
        String uuidRegex =
          "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        String organizationIpaCode = "IPA_TEST_2";
        CreateClientRequest createClientRequest = new CreateClientRequest();
        createClientRequest.setClientName("CLIENTNAME");

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

        doReturn(new ClientDTO(organizationIpaCode + createClientRequest.getClientName(), createClientRequest.getClientName(), organizationIpaCode, uuidRandomForSecret))
                .when(authzServiceMock).registerClient(organizationIpaCode, createClientRequest);

        MvcResult result = mockMvc.perform(
            post("/payhub/auth/clients/{organizationIpaCode}", organizationIpaCode)
              .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
              .contentType(MediaType.APPLICATION_JSON)
              .content(new Gson().toJson(createClientRequest))
          ).andExpect(status().isOk())
          .andReturn();

        ClientDTO clientDTO = new Gson().fromJson(result.getResponse().getContentAsString(), ClientDTO.class);

        Assertions.assertNotNull(result);
        assertEquals(organizationIpaCode + createClientRequest.getClientName(), clientDTO.getClientId());
        assertEquals(createClientRequest.getClientName(), clientDTO.getClientName());
        assertEquals(organizationIpaCode, clientDTO.getOrganizationIpaCode());
        assertTrue(Pattern.compile(uuidRegex).matcher(clientDTO.getClientSecret()).matches());
        assertEquals(uuidRandomForSecret, clientDTO.getClientSecret());
    }

}
