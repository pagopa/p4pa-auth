package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class A2ALegacyClaims2UserInfoMapper {

	private static final String A2A_PREFIX = "A2A-";

	public UserInfo map(String ipaCode) {
		return UserInfo.builder()
			.issuer(ipaCode)
			.userId(A2A_PREFIX + ipaCode)
			.name(ipaCode)
			.familyName(ipaCode)
			.fiscalCode(A2A_PREFIX + ipaCode)
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(ipaCode)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();
	}
}
