package it.gov.pagopa.payhub.auth.mapper;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.auth.utils.Utilities;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.function.Function;

@Service
public class Client2UserInfoMapper implements Function<ClientNoSecretDTO, IamUserInfoDTO> {
	public static final String WS_USER_PREFIX = "WS_USER-";

	@Override
	public IamUserInfoDTO apply(ClientNoSecretDTO clientDTO) {
		return IamUserInfoDTO.builder()
            .type(UserInfo.class.getSimpleName())
            .traceId(Utilities.getTraceId())
			.systemUser(true)
			.issuer(clientDTO.getOrganizationIpaCode())
			.userId(clientDTO.getClientId())
			.innerUserId(clientDTO.getClientId())
			.mappedExternalUserId(buildSystemMappedExternalUserId(clientDTO.getClientId()))
			.name(clientDTO.getClientName())
			.familyName(clientDTO.getOrganizationIpaCode())
			.fiscalCode(clientDTO.getOrganizationIpaCode())
			.organizationAccess(IamUserOrganizationRolesDTO.builder()
				.organizationIpaCode(clientDTO.getOrganizationIpaCode())
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build())
			.build();
	}

	public static String buildSystemMappedExternalUserId(String clientId) {
		return WS_USER_PREFIX + clientId;
	}

	public static boolean isSystemMappedUser(String mappedExternalUserId){
		return mappedExternalUserId.startsWith(WS_USER_PREFIX);
	}

	public static String extractClientId(String mappedExternalUserId){
		return mappedExternalUserId.substring(WS_USER_PREFIX.length());
	}

}
