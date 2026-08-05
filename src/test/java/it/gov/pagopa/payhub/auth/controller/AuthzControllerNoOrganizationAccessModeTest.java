package it.gov.pagopa.payhub.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import io.micrometer.tracing.Tracer;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.security.JwtAuthenticationFilter;
import it.gov.pagopa.payhub.auth.security.WebSecurityConfig;
import it.gov.pagopa.payhub.auth.service.*;
import it.gov.pagopa.payhub.auth.service.m2m.legacy.JWTLegacyHandlerService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthzControllerImpl.class)
@Import({AuthExceptionHandler.class, WebSecurityConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = { "app.enable-access-organization-mode=false" })
class AuthzControllerNoOrganizationAccessModeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthzService authzServiceMock;

    @MockitoBean
    private AuthnService authnServiceMock;

    @MockitoBean
    private ValidateTokenService validateTokenServiceMock;

    @MockitoBean
    private AuditLoggerService auditLoggerServiceMock;

    @MockitoBean
    private JWTLegacyHandlerService jwtLegacyHandlerServiceMock;

    @MockitoBean
    private AccessTokenBuilderService accessTokenBuilderServiceMock;

    @MockitoBean
    private Tracer tracerMock;

// createOperator region
    @Test
    void givenUnauthorizedUserWhenCreateOrganizationOperatorThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        String body = buildAndSerializeCreateOperatorRequest();

        when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("ORG2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
            post("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf((body)))
        ).andExpect(status().isForbidden());
    }

    @Test
    void givenAuthorizedUserWhenCreateOrganizationOperatorThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        String body = buildAndSerializeCreateOperatorRequest();

        when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode(organizationIpaCode)
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        OperatorDTO expectedOperator = new OperatorDTO();
        expectedOperator.operatorId("operatorId");
        when(authzServiceMock.createOrganizationOperator(organizationIpaCode, buildCreateOperatorRequest(), "accessToken"))
                .thenReturn(expectedOperator);

        mockMvc.perform(
            post("/payhub/am/operators/{organizationIpaCode}", organizationIpaCode)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.operatorId").value("operatorId"));
    }

    public static String buildAndSerializeCreateOperatorRequest() {
        Gson gson = new Gson();
        return gson.toJson(buildCreateOperatorRequest());
    }

    private static CreateOperatorRequest buildCreateOperatorRequest() {
        CreateOperatorRequest createOperatorRequest = new CreateOperatorRequest();
        createOperatorRequest.setExternalUserId("externalUserId");
        createOperatorRequest.setFiscalCode("fiscalCode");
        createOperatorRequest.setFirstName("firstName");
        createOperatorRequest.setLastName("lastName");
        createOperatorRequest.setEmail("email@example.com");
        createOperatorRequest.setRoles(List.of("ROLE_ADMIN"));
        return createOperatorRequest;
    }

    // end region

    //createUser region
    @Test
    void givenAuthorizedUserWhenCreateUserThenOk() throws Exception {
        String body = buildCreateUserRequest();

        when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("IPA_TEST_2")
                    .roles(List.of(Constants.ROLE_ADMIN))
                    .build()))
                .build());
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
            post("/payhub/am/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk());
    }

    @Test
    void givenUnauthorizedUserWhenCreateUserThenOk() throws Exception {
        String body = buildCreateUserRequest();

        when(authnServiceMock.getUserInfo("accessToken"))
            .thenReturn(UserInfo.builder()
                .organizations(List.of(UserOrganizationRoles.builder()
                    .organizationIpaCode("IPA_TEST_2")
                    .roles(List.of(Constants.ROLE_OPER))
                    .build()))
                .build());
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
            post("/payhub/am/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf((body)))
        ).andExpect(status().isForbidden());
    }

    public static String buildCreateUserRequest() {
        UserDTO user = new UserDTO();
        user.setExternalUserId("EXTERNALUSERID");
        user.setFiscalCode("FISCALCODE");
        user.setFirstName("FIRSTNAME");
        user.setLastName("LASTNAME");
        Gson gson = new Gson();
        return gson.toJson(user);
    }
    //end region

    //region createClient
    @Test
    void givenUnauthorizedUserWhenRegisterClientThenUnauthorizedException() throws Exception {
        String organizationIpaCode = "IPACODE";
        CreateClientRequest request = buildCreateClientRequest();
        Gson gson = new Gson();
        String body = gson.toJson(request);
        when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(UserInfo.builder()
            .organizations(List.of(UserOrganizationRoles.builder()
              .organizationIpaCode("ORG2")
              .roles(List.of(Constants.ROLE_OPER))
              .build()))
            .build());
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        mockMvc.perform(
          post("/payhub/oauth/clients/{organizationIpaCode}", organizationIpaCode)
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf((body)))
        ).andExpect(status().isForbidden());
    }

    @Test
    void givenAuthorizedUserWhenRegisterClientThenOk() throws Exception {
        String uuidRandomForSecret = UUID.randomUUID().toString();
        String uuidRegex =
          "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        String organizationIpaCode = "IPA_TEST_2";
        CreateClientRequest createClientRequest = buildCreateClientRequest();

        UserInfo expectedUser = UserInfo.builder()
          .userId("USERID")
          .organizationAccess(organizationIpaCode)
          .organizations(List.of(UserOrganizationRoles.builder()
            .organizationIpaCode(organizationIpaCode)
            .roles(List.of(Constants.ROLE_ADMIN))
            .build()))
          .build();

        when(authnServiceMock.getUserInfo("accessToken"))
          .thenReturn(expectedUser);
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");

        doReturn(new ClientDTO(organizationIpaCode + createClientRequest.getClientName(), createClientRequest.getClientName(), organizationIpaCode, uuidRandomForSecret))
                .when(authzServiceMock).registerClient(organizationIpaCode, createClientRequest);

        MvcResult result = mockMvc.perform(
            post("/payhub/oauth/clients/{organizationIpaCode}", organizationIpaCode)
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

    private static CreateClientRequest buildCreateClientRequest() {
        CreateClientRequest createClientRequest = new CreateClientRequest();
        createClientRequest.setClientName("CLIENTNAME");
        return createClientRequest;
    }
    //endregion

    //region getClient
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

        when(authnServiceMock.getUserInfo("accessToken")).thenReturn(expectedUser);
        when(accessTokenBuilderServiceMock.getHeaderPrefix()).thenReturn("accessToken");
        when(authzServiceMock.getClient(organizationIpaCode, clientId))
                .thenReturn(Optional.of(expectedClientDTO));

        MvcResult result = mockMvc.perform(
                        get("/payhub/oauth/clients/{organizationIpaCode}/{clientId}", organizationIpaCode, clientId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                ).andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        ClientDTO actualClientDTO = objectMapper.readValue(jsonResponse, ClientDTO.class);

        assertEquals(expectedClientDTO.getClientId(), actualClientDTO.getClientId());
        assertEquals(expectedClientDTO.getClientName(), actualClientDTO.getClientName());
        assertEquals(expectedClientDTO.getOrganizationIpaCode(), actualClientDTO.getOrganizationIpaCode());
        assertEquals(expectedClientDTO.getClientSecret(), actualClientDTO.getClientSecret());
    }
    //endregion

}
