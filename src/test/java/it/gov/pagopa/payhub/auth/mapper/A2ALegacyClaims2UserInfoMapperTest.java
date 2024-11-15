package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class A2ALegacyClaims2UserInfoMapperTest {

	private A2ALegacyClaims2UserInfoMapper mapper = new A2ALegacyClaims2UserInfoMapper();

	@Test
	void WhenMapThenGetUserInfoMapped() {
		//Given
		String prefix = "A2A-";
		String ipaCode = "ipaCode";
		UserInfo expected = UserInfo.builder()
			.issuer(ipaCode)
			.userId(prefix + ipaCode)
			.name(ipaCode)
			.familyName(ipaCode)
			.fiscalCode(prefix + ipaCode)
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
