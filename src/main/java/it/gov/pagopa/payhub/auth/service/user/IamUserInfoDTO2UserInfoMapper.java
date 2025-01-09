package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.connector.client.OrganizationSearchClient;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class IamUserInfoDTO2UserInfoMapper implements Function<IamUserInfoDTO, UserInfo> {

	public static final String WS_USER_SUFFIX = "-WS_USER";
	private final UsersRepository usersRepository;
	private final OperatorsRepository operatorsRepository;
	private final OrganizationSearchClient organizationSearchClient;

	public IamUserInfoDTO2UserInfoMapper(UsersRepository usersRepository, OperatorsRepository operatorsRepository, OrganizationSearchClient organizationSearchClient) {
		this.usersRepository = usersRepository;
		this.operatorsRepository = operatorsRepository;
		this.organizationSearchClient = organizationSearchClient;
	}

	@Override
	public UserInfo apply(IamUserInfoDTO iamUserInfoDTO) {
		if (iamUserInfoDTO.isSystemUser()) {
			return systemUserMapper(iamUserInfoDTO);
		}
		return userInfoMapper(iamUserInfoDTO);
	}

	private UserInfo systemUserMapper(IamUserInfoDTO iamUserInfoDTO) {
		String organizationIpaCode = iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode();
		return UserInfo.builder()
			.userId(iamUserInfoDTO.getUserId())
			.mappedExternalUserId(buildSystemMappedExternalUserId(organizationIpaCode))
			.fiscalCode(iamUserInfoDTO.getFiscalCode())
			.familyName(iamUserInfoDTO.getFamilyName())
			.name(iamUserInfoDTO.getName())
			.issuer(iamUserInfoDTO.getIssuer())
			.organizations(Collections.singletonList(UserOrganizationRoles.builder()
				.organizationIpaCode(organizationIpaCode)
				.roles(Collections.singletonList(Constants.ROLE_ADMIN))
				.build()))
			.build();
	}

	public static String buildSystemMappedExternalUserId(String organizationIpaCode) {
		return organizationIpaCode + WS_USER_SUFFIX;
	}

	private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO) {
		User user = usersRepository.findById(iamUserInfoDTO.getInnerUserId()).orElseThrow(() -> new UserNotFoundException("Cannot found user having inner id:" + iamUserInfoDTO.getInnerUserId()));
		List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());

		String orgIpaCode = Optional.ofNullable(iamUserInfoDTO.getOrganizationAccess())
				.map(IamUserOrganizationRolesDTO::getOrganizationIpaCode)
				.orElseGet(() -> !userRoles.isEmpty() ? userRoles.get(0).getOrganizationIpaCode() : null);

		Broker brokerInfo = null;
		if (orgIpaCode != null) {
			Organization organization = organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, String.valueOf(iamUserInfoDTO.getOrganizationAccess()));
			if (organization != null && organization.getBrokerId() != null) {
				brokerInfo = organizationSearchClient.getBrokerById(organization.getBrokerId(), String.valueOf(iamUserInfoDTO.getOrganizationAccess()));
			}
		}

		UserInfo userInfo = UserInfo.builder()
			.userId(user.getUserId())
			.mappedExternalUserId(user.getMappedExternalUserId())
			.fiscalCode(iamUserInfoDTO.getFiscalCode())
			.familyName(iamUserInfoDTO.getFamilyName())
			.name(iamUserInfoDTO.getName())
			.issuer(iamUserInfoDTO.getIssuer())
			.organizations(userRoles.stream()
				.map(r -> UserOrganizationRoles.builder()
					.operatorId(r.getOperatorId())
					.organizationIpaCode(r.getOrganizationIpaCode())
					.roles(new ArrayList<>(r.getRoles()))
					.email(r.getEmail())
					.build())
				.toList())
			.build();

		if(iamUserInfoDTO.getOrganizationAccess() != null){
			userInfo.setOrganizationAccess(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
		}
		if (brokerInfo != null) {
			userInfo.setBrokerId(brokerInfo.getBrokerId());
			userInfo.setBrokerFiscalCode(brokerInfo.getBrokerFiscalCode());
		}
		return userInfo;
	}

}
