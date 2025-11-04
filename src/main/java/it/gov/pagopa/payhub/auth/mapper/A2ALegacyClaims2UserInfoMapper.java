package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.BaseUserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class A2ALegacyClaims2UserInfoMapper {

	private static final String A2A_PREFIX = "A2A-";

	public UserInfo map(String ipaCode) {
		return BaseUserInfo.builder()
			.systemUser(true)
			.issuer(ipaCode)
			.userId(A2A_PREFIX + ipaCode)
			.mappedExternalUserId(buildA2AMappedExternalUserId(ipaCode))
			.name("A2A")
			.familyName(ipaCode)
			.fiscalCode(ipaCode)
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(ipaCode)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();
	}

	private String buildA2AMappedExternalUserId(String orgIpaCode) {
		return A2A_PREFIX + orgIpaCode;
	}

	public static boolean isA2AMappedUser(String mappedExternalUserId){
		return mappedExternalUserId.startsWith(A2A_PREFIX);
	}

	public static String extractOrgIpaCode(String mappedExternalUserId){
		return mappedExternalUserId.substring(A2A_PREFIX.length());
	}
}
