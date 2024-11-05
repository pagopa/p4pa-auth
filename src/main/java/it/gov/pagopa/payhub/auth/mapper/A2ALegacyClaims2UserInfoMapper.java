package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class A2ALegacyClaims2UserInfoMapper {

	private static final String A2A_PREFIX = "A2A-";

	public IamUserInfoDTO map(String subject) {
		return IamUserInfoDTO.builder()
			.systemUser(true)
			.issuer(subject)
			.userId(A2A_PREFIX + subject)
			.name(subject)
			.familyName(subject)
			.fiscalCode(subject)
			.organizationAccess(IamUserOrganizationRolesDTO.builder()
				.organizationIpaCode(subject)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build())
			.build();
	}
}
