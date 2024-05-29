package it.gov.pagopa.payhub.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.service.AuthService;
import it.gov.pagopa.payhub.model.generated.AuthErrorDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControllerImpl.class)
@Import({AuthExceptionHandler.class})
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    AuthService authServiceMock;

    @Test
    void givenExpectedAuthTokenThenOk() throws Exception {
        String clientId="CLIENT_ID";
        String grantType="GRANT_TYPE";
        String subjectToken="SUBJECT_TOKEN";
        String subjectIssuer="SUBJECT_ISSUER";
        String subjectTokenType="SUBJECT_TOKEN_TYPE";
        String scope="SCOPE";

        doNothing().when(authServiceMock).postToken(clientId,grantType,subjectToken,subjectIssuer,subjectTokenType,scope);

        MvcResult result = mockMvc.perform(
                post("/payhub/auth/token")
                        .param("client_id", clientId)
                        .param("grant_type", grantType)
                        .param("subject_token", subjectToken)
                        .param("subject_issuer", subjectIssuer)
                        .param("subject_token_type", subjectTokenType)
                        .param("scope", scope)
        ).andExpect(status().is2xxSuccessful()).andReturn();

        Assertions.assertNotNull(result);
    }

    @Test
    void givenRequestWithoutAuthTokenThenBadRequest() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/payhub/auth/token")
        ).andExpect(status().isBadRequest()).andReturn();

        AuthErrorDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                AuthErrorDTO.class);
        assertEquals(AuthErrorDTO.ErrorEnum.INVALID_REQUEST, actual.getError());
    }
}
