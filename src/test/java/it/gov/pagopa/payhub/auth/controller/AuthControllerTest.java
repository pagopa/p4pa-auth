package it.gov.pagopa.payhub.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.auth.exception.ValidationExceptionHandler;
import it.gov.pagopa.payhub.auth.service.AuthService;
import openapi.pagopa.payhub.model.AuthErrorDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControllerImpl.class)
@Import({ValidationExceptionHandler.class})
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    AuthService authServiceMock;

    @Test
    void givenExpectedAuthTokenThenOk() throws Exception {
        doNothing().when(authServiceMock).authToken("token");

        MvcResult result = mockMvc.perform(
                get("/auth")
                        .param("token", "token")
        ).andExpect(status().is2xxSuccessful()).andReturn();

        Assertions.assertNotNull(result);
    }

    @Test
    void givenRequestWithoutAuthTokenThenBadRequest() throws Exception {
        doNothing().when(authServiceMock).authToken("token");

        MvcResult result = mockMvc.perform(
                get("/auth")
        ).andExpect(status().isBadRequest()).andReturn();

        AuthErrorDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                AuthErrorDTO.class);
        assertEquals(AuthErrorDTO.CodeEnum.INVALID_REQUEST, actual.getCode());
    }
}
