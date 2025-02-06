package it.gov.pagopa.payhub.auth.service.user;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.auth.connector.organization.BrokerService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IamUserInfoDTO2UserInfoMapper {

    private final OperatorsRepository operatorsRepository;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;
    private final boolean organizationAccessMode;

    public IamUserInfoDTO2UserInfoMapper(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,

            OperatorsRepository operatorsRepository,
            OrganizationService organizationService,
            BrokerService brokerService
    ) {
        this.operatorsRepository = operatorsRepository;
        this.organizationService = organizationService;
        this.brokerService = brokerService;
        this.organizationAccessMode = organizationAccessMode;
    }

    public UserInfo apply(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        if (iamUserInfoDTO.isSystemUser()) {
            return systemUserMapper(iamUserInfoDTO, accessToken);
        }
        return userInfoMapper(iamUserInfoDTO, accessToken);
    }

    private UserInfo systemUserMapper(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        String organizationIpaCode = iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode();
        UserInfo userInfo = UserInfo.builder()
                .systemUser(true)
                .userId(iamUserInfoDTO.getInnerUserId())
                .mappedExternalUserId(iamUserInfoDTO.getMappedExternalUserId())
                .fiscalCode(iamUserInfoDTO.getFiscalCode())
                .familyName(iamUserInfoDTO.getFamilyName())
                .name(iamUserInfoDTO.getName())
                .issuer(iamUserInfoDTO.getIssuer())
                .organizations(Collections.singletonList(UserOrganizationRoles.builder()
                        .organizationId(retrieveOrganizationId(organizationIpaCode, accessToken))
                        .organizationIpaCode(organizationIpaCode)
                        .roles(Collections.singletonList(Constants.ROLE_ADMIN))
                        .build()))
                .build();
        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);
        return userInfo;
    }

    private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());

        UserInfo userInfo = UserInfo.builder()
                .systemUser(false)
                .userId(iamUserInfoDTO.getInnerUserId())
                .mappedExternalUserId(iamUserInfoDTO.getMappedExternalUserId())
                .fiscalCode(iamUserInfoDTO.getFiscalCode())
                .familyName(iamUserInfoDTO.getFamilyName())
                .name(iamUserInfoDTO.getName())
                .issuer(iamUserInfoDTO.getIssuer())
                .organizations(userRoles.stream()
                        .map(r -> (UserOrganizationRoles) UserOrganizationRoles.builder()
                                .operatorId(r.getOperatorId())
                                .organizationIpaCode(r.getOrganizationIpaCode())
                                .roles(new ArrayList<>(r.getRoles()))
                                .email(r.getEmail())
                                .organizationId(retrieveOrganizationId(r.getOrganizationIpaCode(), accessToken))
                                .build())
                        .toList())
                .build();

        if (iamUserInfoDTO.getOrganizationAccess() != null) {
            userInfo.setOrganizationAccess(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
        }
        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);
        userInfo.setCanManageUsers(!organizationAccessMode);
        return userInfo;
    }

    private Long retrieveOrganizationId(String organizationIpaCode, String accessToken) {
        if (StringUtils.isNotBlank(organizationIpaCode)) {
            Organization organization = organizationService.getOrganizationByIpaCode(organizationIpaCode, accessToken);
            if (organization != null) {
                return organization.getOrganizationId();
            }
        }
        return null;
    }

    private Broker getSessionBroker(IamUserInfoDTO iamUserInfoDTO, List<UserOrganizationRoles> userOrganizations, String accessToken) {
        String orgIpaCode = Optional.ofNullable(iamUserInfoDTO.getOrganizationAccess())
                .map(IamUserOrganizationRolesDTO::getOrganizationIpaCode)
                .orElseGet(() -> userOrganizations.isEmpty() ? null : userOrganizations.getFirst().getOrganizationIpaCode());

        if (orgIpaCode != null) {
            Organization organization = organizationService.getOrganizationByIpaCode(orgIpaCode, accessToken);
            if (organization != null && organization.getBrokerId() != null) {
                return brokerService.getBrokerById(organization.getBrokerId(), accessToken);
            }
        }
        return null;
    }

    private void setBrokerInfo(UserInfo userInfo, IamUserInfoDTO iamUserInfo, String accessToken) {
        Broker brokerInfo = getSessionBroker(iamUserInfo, userInfo.getOrganizations(), accessToken);

        if (brokerInfo != null) {
            userInfo.setBrokerId(brokerInfo.getBrokerId());
            userInfo.setBrokerFiscalCode(brokerInfo.getBrokerFiscalCode());
        }
    }

}
