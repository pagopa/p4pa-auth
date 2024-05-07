package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControllerImpl.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authServiceMock;

    @Test
    void authToken() throws Exception {
        doNothing().when(authServiceMock).authToken("");

        MvcResult result = mockMvc.perform(
                get("payhub/auth/")
                        .param("token", "token")
        ).andExpect(status().is2xxSuccessful()).andReturn();
    }
}
