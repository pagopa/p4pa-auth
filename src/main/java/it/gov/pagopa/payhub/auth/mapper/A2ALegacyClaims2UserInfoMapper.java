package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class A2ALegacyClaims2UserInfoMapper {

	public IamUserInfoDTO map(String subject) {
		return IamUserInfoDTO.builder()
			.systemUser(true)
			.issuer(subject)
			.userId(UUID.randomUUID().toString())
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
