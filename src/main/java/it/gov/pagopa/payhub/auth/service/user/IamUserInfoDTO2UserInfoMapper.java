package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
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
import java.util.Optional;
import java.util.function.Function;

@Service
public class IamUserInfoDTO2UserInfoMapper implements Function<IamUserInfoDTO, UserInfo> {

    private final UsersRepository usersRepository;
    private final OperatorsRepository operatorsRepository;

    public IamUserInfoDTO2UserInfoMapper(UsersRepository usersRepository, OperatorsRepository operatorsRepository) {
        this.usersRepository = usersRepository;
        this.operatorsRepository = operatorsRepository;
    }

    @Override
    public UserInfo apply(IamUserInfoDTO iamUserInfoDTO) {
        UserInfo userInfo = UserInfo.builder()
                .fiscalCode(iamUserInfoDTO.getFiscalCode())
                .familyName(iamUserInfoDTO.getFamilyName())
                .name(iamUserInfoDTO.getName())
                .issuer(iamUserInfoDTO.getIssuer())
                .build();
        if (iamUserInfoDTO.isSystemUser()) {
            userInfo.setUserId(iamUserInfoDTO.getUserId());
            userInfo.setMappedExternalUserId(iamUserInfoDTO.getFiscalCode());
            UserOrganizationRoles userOrgRoles = UserOrganizationRoles.builder()
              .organizationIpaCode(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode())
              .roles(Collections.singletonList(Constants.ROLE_ADMIN))
              .build();
            userInfo.setOrganizations(Collections.singletonList(userOrgRoles));
        } else {
            User user = usersRepository.findById(iamUserInfoDTO.getInnerUserId()).orElseThrow(() -> new UserNotFoundException("Cannot found user having inner id:" + iamUserInfoDTO.getInnerUserId()));
            List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());
            userInfo.setUserId(user.getUserId());
            userInfo.setMappedExternalUserId(user.getMappedExternalUserId());
            userInfo.setOrganizations(userRoles.stream()
              .map(r -> UserOrganizationRoles.builder()
                .operatorId(r.getOperatorId())
                .organizationIpaCode(r.getOrganizationIpaCode())
                .roles(new ArrayList<>(r.getRoles()))
                .email(r.getEmail())
                .build())
              .toList());
            Optional.ofNullable(iamUserInfoDTO.getOrganizationAccess())
              .map(IamUserOrganizationRolesDTO::getOrganizationIpaCode)
              .ifPresent(userInfo::setOrganizationAccess);
        }
        return userInfo;
    }
}
