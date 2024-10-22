package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.function.Function;

@Service
public class ClientDTO2UserInfoMapper implements Function<ClientDTO, IamUserInfoDTO> {
	@Override
	public IamUserInfoDTO apply(ClientDTO clientDTO) {
		return IamUserInfoDTO.builder()
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
			.build();
	}
}
