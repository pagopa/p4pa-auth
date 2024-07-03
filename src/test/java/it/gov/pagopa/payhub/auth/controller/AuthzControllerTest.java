package it.gov.pagopa.payhub.auth.controller;

import it.gov.pagopa.payhub.auth.config.JsonConfig;
import it.gov.pagopa.payhub.auth.exception.AuthExceptionHandler;
import it.gov.pagopa.payhub.auth.service.AuthzService;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthzControllerImpl.class)
@Import({AuthExceptionHandler.class, JsonConfig.class})
class AuthzControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthzService authzServiceMock;

//region desc=getOrganizationOperators tests
    @Test
    void whenGetOrganizationOperatorsThenOk() throws Exception {
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(4, 1);

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
//end region
}
