package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClientDTO2UserInfoMapperTest {

	@InjectMocks
	private ClientDTO2UserInfoMapper mapper;

	@Test
	void givenDTOWhenApplyTheOk() {
		// Given
		String plainClientSecret = UUID.randomUUID().toString();
		String organizationIpaCode = "organizationIpaCode";
		String clientName = "clientName";
		String clientId = organizationIpaCode + clientName;

		ClientDTO clientDTO = ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(plainClientSecret)
			.build();

		// When
		IamUserInfoDTO result = mapper.apply(clientDTO);
		//Then
		Assertions.assertEquals(
			IamUserInfoDTO.builder()
				.systemUser(true)
				.issuer(clientDTO.getOrganizationIpaCode())
				.userId(clientDTO.getClientId())
				.name(clientDTO.getClientName())
				.familyName(clientDTO.getOrganizationIpaCode())
				.fiscalCode(clientDTO.getOrganizationIpaCode())
				.organizationAccess(IamUserOrganizationRolesDTO.builder()
					.organizationIpaCode(clientDTO.getOrganizationIpaCode())
					.roles(Collections.singletonList(Constants.ROLE_ADMIN))
					.build())
				.build(),
			result
		);
	}
}
