package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class A2ALegacyClaims2UserInfoMapperTest {

	@InjectMocks
	private A2ALegacyClaims2UserInfoMapper mapper;

	@Test
	void WhenMapThenGetUserInfoMapped() {
		//Given
		String prefix = "A2A-";
		String subject = "subject";
		IamUserInfoDTO expected = IamUserInfoDTO.builder()
			.systemUser(true)
			.issuer(subject)
			.userId(prefix + subject)
			.name(subject)
			.familyName(subject)
			.fiscalCode(subject)
			.organizationAccess(IamUserOrganizationRolesDTO.builder()
				.organizationIpaCode(subject)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build())
			.build();

		//When
		IamUserInfoDTO result = mapper.map(subject);
		//Then
		Assertions.assertEquals(expected,	result);
	}
}
