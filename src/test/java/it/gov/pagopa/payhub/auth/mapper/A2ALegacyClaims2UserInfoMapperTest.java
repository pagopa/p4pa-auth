package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class A2ALegacyClaims2UserInfoMapperTest {

	private final A2ALegacyClaims2UserInfoMapper mapper = new A2ALegacyClaims2UserInfoMapper();

	@Test
	void WhenMapThenGetUserInfoMapped() {
		//Given
		String prefix = "A2A-";
		String ipaCode = "ipaCode";
		UserInfo expected = UserInfo.builder()
			.systemUser(true)
			.issuer(ipaCode)
			.userId(prefix + ipaCode)
			.mappedExternalUserId(prefix + ipaCode)
			.name("A2A")
			.familyName(ipaCode)
			.fiscalCode(ipaCode)
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(ipaCode)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();

		//When
		UserInfo result = mapper.map(ipaCode);
		//Then
		Assertions.assertEquals(expected,	result);
	}
}
