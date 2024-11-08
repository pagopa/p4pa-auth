package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class A2ALegacyClaims2UserInfoMapper {

	private static final String A2A_PREFIX = "A2A-";

	public UserInfo map(String issuer) {
		return UserInfo.builder()
			.issuer(issuer)
			.userId(A2A_PREFIX + issuer)
			.name(issuer)
			.familyName(issuer)
			.fiscalCode(A2A_PREFIX + issuer)
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(issuer)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();
	}
}
