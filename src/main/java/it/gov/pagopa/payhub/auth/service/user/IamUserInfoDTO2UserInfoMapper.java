package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.connector.client.OrganizationSearchClient;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
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

    public static final String WS_USER_SUFFIX = "-WS_USER";
    private final UsersRepository usersRepository;
    private final OperatorsRepository operatorsRepository;
    private final OrganizationSearchClient organizationSearchClient;
    private final boolean organizationAccessMode;
    private final TokenStoreService tokenStoreService;

    public IamUserInfoDTO2UserInfoMapper(@Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,
                                         UsersRepository usersRepository,
                                         OperatorsRepository operatorsRepository,
                                         OrganizationSearchClient organizationSearchClient,
                                         TokenStoreService tokenStoreService) {
        this.usersRepository = usersRepository;
        this.operatorsRepository = operatorsRepository;
        this.organizationSearchClient = organizationSearchClient;
        this.organizationAccessMode = organizationAccessMode;
        this.tokenStoreService = tokenStoreService;
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
        setBrokerInfo(userInfo, accessToken);
        return userInfo;
    }

    public static String buildSystemMappedExternalUserId(String organizationIpaCode) {
        return organizationIpaCode + WS_USER_SUFFIX;
    }

    private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
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

        if (iamUserInfoDTO.getOrganizationAccess() != null) {
            userInfo.setOrganizationAccess(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
        }
        setBrokerInfo(userInfo, accessToken);
        userInfo.setCanManageUsers(!organizationAccessMode);
        return userInfo;
    }

    private Broker getSessionBroker(IamUserInfoDTO iamUserInfoDTO, List<Operator> userRoles, String accessToken) {
        String orgIpaCode = Optional.ofNullable(iamUserInfoDTO.getOrganizationAccess())
                .map(IamUserOrganizationRolesDTO::getOrganizationIpaCode)
                .orElseGet(() -> !userRoles.isEmpty() ? userRoles.get(0).getOrganizationIpaCode() : null);

        if (orgIpaCode != null) {
            Organization organization = organizationSearchClient.getOrganizationByIpaCode(orgIpaCode, accessToken);
            if (organization != null && organization.getBrokerId() != null) {
                return organizationSearchClient.getBrokerById(organization.getBrokerId(), accessToken);
            }
        }
        return null;
    }

    private void setBrokerInfo(UserInfo userInfo, String accessToken) {
        IamUserInfoDTO iamUserInfo = tokenStoreService.load(accessToken);
        if (iamUserInfo == null) {
            throw new InvalidAccessTokenException("AccessToken not found");
        }

        List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfo.getInnerUserId());
        Broker brokerInfo = getSessionBroker(iamUserInfo, userRoles, accessToken);

        if (brokerInfo != null) {
            userInfo.setBrokerId(brokerInfo.getBrokerId());
            userInfo.setBrokerFiscalCode(brokerInfo.getBrokerFiscalCode());
        } else {
            throw new IllegalStateException("Broker information not found for the user.");
        }
    }

}
