package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Service
public class IamUserInfoDTO2UserInfoMapper implements Function<IamUserInfoDTO, UserInfo> {

	public static final String WS_USER_SUFFIX = "-WS_USER";

	private final UsersRepository usersRepository;
	private final OperatorsRepository operatorsRepository;

	public IamUserInfoDTO2UserInfoMapper(UsersRepository usersRepository, OperatorsRepository operatorsRepository) {
		this.usersRepository = usersRepository;
		this.operatorsRepository = operatorsRepository;
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
			.mappedExternalUserId(organizationIpaCode + WS_USER_SUFFIX)
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

	private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO) {
		User user = usersRepository.findById(iamUserInfoDTO.getInnerUserId()).orElseThrow(() -> new UserNotFoundException("Cannot found user having inner id:" + iamUserInfoDTO.getInnerUserId()));
		List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());
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
		return userInfo;
	}
}
