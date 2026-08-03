package it.gov.pagopa.payhub.auth.service.user;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.auth.connector.organization.BrokerService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.connector.organization.OrgSubUnitService;
import it.gov.pagopa.payhub.auth.utils.Constants;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import it.gov.pagopa.payhub.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Broker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.OrgSubUnit;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class IamUserInfoDTO2UserInfoMapper {

    private final OperatorsRepository operatorsRepository;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;
    private final OrgSubUnitService orgSubUnitService;
    private final boolean organizationAccessMode;

    public IamUserInfoDTO2UserInfoMapper(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,
            OperatorsRepository operatorsRepository,
            OrganizationService organizationService,
            BrokerService brokerService,
            OrgSubUnitService orgSubUnitService
    ) {
        this.operatorsRepository = operatorsRepository;
        this.organizationService = organizationService;
        this.brokerService = brokerService;
        this.orgSubUnitService = orgSubUnitService;
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

        Optional<Organization> organization =
                retrieveOrganization(organizationIpaCode, accessToken);

        UserInfo userInfo;

        if (isLimitedScope(iamUserInfoDTO)) {
            userInfo = UserInfoLimitedScope.builder()
                    .organizations(Collections.emptyList())
                    .canManageUsers(false)
                    .resource(iamUserInfoDTO.getResource())
                    .build();
        } else {
            Long organizationId = organization
                    .map(Organization::getOrganizationId)
                    .orElse(null);

            List<String> orgSubUnitCodes = retrieveAllOrgSubUnitCodes(organizationId, accessToken);

            UserOrganizationRoles organizationRoles =
                    UserOrganizationRoles.builder()
                            .operatorId(iamUserInfoDTO.getInnerUserId())
                            .organizationId(organizationId)
                            .organizationIpaCode(organizationIpaCode)
                            .organizationFiscalCode(organization
                                            .map(Organization::getOrgFiscalCode)
                                            .orElse(null)
                            )
                            .roles(Collections.singletonList(Constants.ROLE_ADMIN))
                            .email(organization.map(Organization::getOrgEmail)
                                    .orElse(null)
                            )
                            .orgSubUnitCodes(orgSubUnitCodes)
                            .build();

            userInfo = UserInfo.builder()
                    .organizationAccess(organizationIpaCode)
                    .canManageUsers(false)
                    .organizations(
                            Collections.singletonList(organizationRoles)
                    )
                    .build();
        }

        setCommonFieldsForSystemUser(userInfo, iamUserInfoDTO);
        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);

        return userInfo;
    }

    private UserInfo userInfoMapper(IamUserInfoDTO iamUserInfoDTO, String accessToken) {
        List<Operator> userRoles = operatorsRepository.findAllByUserId(iamUserInfoDTO.getInnerUserId());

        UserInfo userInfo;

        if (isLimitedScope(iamUserInfoDTO)) {
            userInfo = UserInfoLimitedScope.builder()
                    .organizations(Collections.emptyList())
                    .resource(iamUserInfoDTO.getResource())
                    .build();
        } else {
            List<UserOrganizationRoles> organizations = userRoles.stream()
                    .map(operator -> mapUserOrganizationRoles(operator, iamUserInfoDTO.getMappedExternalUserId(), accessToken))
                    .toList();

            userInfo = UserInfo.builder()
                    .organizations(organizations)
                    .build();
        }

        setCommonFieldsForStandardUser(userInfo, iamUserInfoDTO);

        if (iamUserInfoDTO.getOrganizationAccess() != null) {
            userInfo.setOrganizationAccess(iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
        }
        setBrokerInfo(userInfo, iamUserInfoDTO, accessToken);
        userInfo.setCanManageUsers(!organizationAccessMode);
        return userInfo;
    }

    private UserOrganizationRoles mapUserOrganizationRoles(Operator operator, String operatorExternalUserId, String accessToken) {
        Optional<Organization> organizationOpt =
                retrieveOrganization(operator.getOrganizationIpaCode(), accessToken);

        List<String> orgSubUnitCodes = organizationOpt
                .map(Organization::getOrganizationId)
                .map(organizationId -> retrieveOperatorOrgSubUnitCodes(
                        organizationId,
                        operatorExternalUserId,
                        accessToken))
                .orElseGet(Collections::emptyList);

        String organizationFiscalCode = organizationOpt
                .map(Organization::getOrgFiscalCode)
                .orElse(null);

        return UserOrganizationRoles.builder()
                .operatorId(operator.getOperatorId())
                .organizationId(organizationOpt
                        .map(Organization::getOrganizationId)
                        .orElse(null))
                .organizationIpaCode(operator.getOrganizationIpaCode())
                .organizationFiscalCode(organizationFiscalCode)
                .roles(new ArrayList<>(operator.getRoles()))
                .email(operator.getEmail())
                .orgSubUnitCodes(orgSubUnitCodes)
                .build();
    }

    private List<String> retrieveAllOrgSubUnitCodes(Long organizationId, String accessToken) {
        if (organizationId == null) {
            return Collections.emptyList();
        }

        List<OrgSubUnit> orgSubUnits = orgSubUnitService.getActiveOrgSubUnitsByOrganizationId(organizationId, accessToken);

        return extractOrgSubUnitCodes(orgSubUnits);
    }

    private List<String> retrieveOperatorOrgSubUnitCodes(Long organizationId, String operatorExternalUserId, String accessToken) {
        if (organizationId == null || StringUtils.isBlank(operatorExternalUserId)) {
            return Collections.emptyList();
        }

        List<OrgSubUnit> orgSubUnits =
                orgSubUnitService.getActiveOrgSubUnitsByOrganizationIdAndOperatorExternalUserId(organizationId, operatorExternalUserId, accessToken);

        return extractOrgSubUnitCodes(orgSubUnits);
    }

    private List<String> extractOrgSubUnitCodes(List<OrgSubUnit> orgSubUnits) {
        if (CollectionUtils.isEmpty(orgSubUnits)) {
            return Collections.emptyList();
        }

        return orgSubUnits.stream()
                .filter(Objects::nonNull)
                .map(OrgSubUnit::getSubUnitCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
    }

    private boolean isLimitedScope(IamUserInfoDTO iamUserInfoDTO) {
        return iamUserInfoDTO.getType() != null
                && UserInfoLimitedScope.class.getSimpleName().equals(iamUserInfoDTO.getType());
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
