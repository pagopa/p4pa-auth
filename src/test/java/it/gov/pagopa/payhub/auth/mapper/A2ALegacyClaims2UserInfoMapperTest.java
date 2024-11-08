package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
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
		String issuer = "issuer";
		UserInfo expected = UserInfo.builder()
			.issuer(issuer)
			.userId(prefix + issuer)
			.name(issuer)
			.familyName(issuer)
			.fiscalCode(prefix + issuer)
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(issuer)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();

		//When
		UserInfo result = mapper.map(issuer);
		//Then
		Assertions.assertEquals(expected,	result);
	}
}
