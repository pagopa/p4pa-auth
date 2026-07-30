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
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
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
        Optional<Organization> organization = retrieveOrganization(organizationIpaCode, accessToken);
        UserInfo userInfo;

        if (
            iamUserInfoDTO.getType() != null
            && UserInfoLimitedScope.class.getSimpleName().equals(iamUserInfoDTO.getType())
        ) {
            userInfo = UserInfoLimitedScope.builder()
                    .organizations(Collections.emptyList())
                    .canManageUsers(false) // system user => false (as before)
                    .resource(iamUserInfoDTO.getResource())
                    .build();
        } else {
            userInfo = UserInfo.builder()
                    .organizationAccess(organizationIpaCode)
                    .canManageUsers(false) // system user => false (as before)
                    .organizations(Collections.singletonList(
                            UserOrganizationRoles.builder()
                                    .operatorId(iamUserInfoDTO.getInnerUserId())
                                    .organizationId(organization.map(Organization::getOrganizationId).orElse(null))
                                    .organizationIpaCode(organizationIpaCode)
                                    .organizationFiscalCode(organization.map(Organization::getOrgFiscalCode).orElse(null))
                                    .roles(Collections.singletonList(Constants.ROLE_ADMIN))
                                    .email(organization.map(Organization::getOrgEmail).orElse(null))
                                    .build()))
                    .build();
        }

        setCommonFieldsForSystemUser(userInfo, iamUserInfoDTO);

        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);

        updateOrganizationExternalId(iamUserInfoDTO, accessToken);

        return userInfo;
    }

    private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());

        UserInfo userInfo;

        if (
                iamUserInfoDTO.getType() != null
                        && UserInfoLimitedScope.class.getSimpleName().equals(iamUserInfoDTO.getType())
        ) {
            userInfo = UserInfoLimitedScope.builder()
                    .organizations(Collections.emptyList())
                    .resource(iamUserInfoDTO.getResource())
                    .build();
        } else {
            userInfo = UserInfo.builder()
                    .organizations(userRoles.stream()
                            .map(r -> {
                                Optional<Organization> organizationOpt = retrieveOrganization(r.getOrganizationIpaCode(), accessToken);
                                return (UserOrganizationRoles) UserOrganizationRoles.builder()
                                        .operatorId(r.getOperatorId())
                                        .organizationId(organizationOpt.map(Organization::getOrganizationId).orElse(null))
                                        .organizationIpaCode(r.getOrganizationIpaCode())
                                        .organizationFiscalCode(organizationOpt.map(Organization::getOrgFiscalCode).orElse(null))
                                        .roles(new ArrayList<>(r.getRoles()))
                                        .email(r.getEmail())
                                        .build();
                            })
                            .toList())
                    .build();
        }

        setCommonFieldsForStandardUser(userInfo, iamUserInfoDTO);

        if (iamUserInfoDTO.getOrganizationAccess() != null) {
            userInfo.setOrganizationAccess(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
            updateOrganizationExternalId(iamUserInfoDTO, accessToken);
        }
        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);
        userInfo.setCanManageUsers(!organizationAccessMode);
        return userInfo;
    }

    private void setCommonFieldsForSystemUser(UserInfo target, IamUserInfoDTO dto) {
        target.setType(dto.getType());
        target.setTraceId(dto.getTraceId());
        target.setSystemUser(true);
        target.setUserId(dto.getInnerUserId());
        target.setMappedExternalUserId(dto.getMappedExternalUserId());
        target.setFiscalCode(dto.getFiscalCode());
        target.setFamilyName(dto.getFamilyName());
        target.setName(dto.getName());
        target.setIssuer(dto.getIssuer());
    }

    private void setCommonFieldsForStandardUser(UserInfo target, IamUserInfoDTO dto) {
        target.setType(dto.getType());
        target.setTraceId(dto.getTraceId());
        target.setSystemUser(false);
        target.setUserId(dto.getInnerUserId());
        target.setMappedExternalUserId(dto.getMappedExternalUserId());
        target.setFiscalCode(dto.getFiscalCode());
        target.setFamilyName(dto.getFamilyName());
        target.setName(dto.getName());
        target.setIssuer(dto.getIssuer());
    }

    private Optional<Organization> retrieveOrganization(String organizationIpaCode, String accessToken) {
        if (StringUtils.isNotBlank(organizationIpaCode)) {
            return Optional.ofNullable(organizationService.getOrganizationByIpaCode(organizationIpaCode, accessToken));
        }
        return Optional.empty();
    }

    private void updateOrganizationExternalId(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        IamUserOrganizationRolesDTO organizationRolesDTO = iamUserInfoDTO.getOrganizationAccess();
        if (organizationRolesDTO == null) {
            return;
        }

        Organization organization = organizationService.getOrganizationByIpaCode(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode(), accessToken);
        if(organization == null) {
            return;
        }

        String externalOrgId = organizationRolesDTO.getExternalOrganizationId();
        if (externalOrgId != null && !externalOrgId.equals(organization.getExternalOrganizationId())) {
            organizationService.updateOrganizationExternalId(organization.getOrganizationId(), externalOrgId, accessToken);
        }
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
        List<UserOrganizationRoles> userOrganizations;

        if (UserInfoLimitedScope.class.getSimpleName().equals(iamUserInfo.getType())) {
            userOrganizations = List.of(iamUserInfo.getResource().getOrganization());
        } else {
            userOrganizations = userInfo.getOrganizations();
        }
        Broker brokerInfo = getSessionBroker(iamUserInfo, userOrganizations, accessToken);

        if (brokerInfo != null) {
            userInfo.setBrokerId(brokerInfo.getBrokerId());
            userInfo.setBrokerFiscalCode(brokerInfo.getBrokerFiscalCode());
        }
    }

}
