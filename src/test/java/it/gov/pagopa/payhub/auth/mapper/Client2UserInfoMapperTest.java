package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.auth.utils.TestUtils;
import it.gov.pagopa.payhub.auth.utils.UtilitiesTest;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class Client2UserInfoMapperTest {


	private final Client2UserInfoMapper mapper = new Client2UserInfoMapper();

    @BeforeEach
    void setUp() {
        UtilitiesTest.setTraceId("traceId");
    }

    @AfterEach
    void tearDown() {
        UtilitiesTest.clearTraceIdContext();
    }

	@Test
	void givenDTOWhenApplyTheOk() {
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;

		ClientNoSecretDTO clientDTO = ClientNoSecretDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.build();
		IamUserInfoDTO iamUserInfoDTO = IamUserInfoDTO.builder()
            .type("UserInfo")
            .traceId("traceId")
			.systemUser(true)
			.mappedExternalUserId("WS_USER-" + clientDTO.getClientId())
			.innerUserId(clientDTO.getClientId())
			.issuer(clientDTO.getOrganizationIpaCode())
			.userId(clientDTO.getClientId())
			.name(clientDTO.getClientName())
			.familyName(clientDTO.getOrganizationIpaCode())
			.fiscalCode(clientDTO.getOrganizationIpaCode())
			.organizationAccess(IamUserOrganizationRolesDTO.builder()
				.organizationIpaCode(clientDTO.getOrganizationIpaCode())
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build())
			.build();
		// When
		IamUserInfoDTO result = mapper.apply(clientDTO);
		//Then
		Assertions.assertEquals(iamUserInfoDTO,	result);
		TestUtils.checkNotNullFields(result, "scope", "resource");
	}
}
