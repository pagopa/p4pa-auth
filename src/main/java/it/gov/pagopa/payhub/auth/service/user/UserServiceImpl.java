package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.connector.OrganizationClientImpl;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.OperatorRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OrganizationOperatorRetrieverService;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelBroker;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.EntityModelOrganization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private OrganizationClientImpl organizationClient;
    private final TokenStoreService tokenStoreService;
    private final UserRegistrationService userRegistrationService;
    private final OperatorRegistrationService operatorRegistrationService;
    private final IamUserInfoDTO2UserInfoMapper userInfoMapper;
    private final OrganizationOperatorRetrieverService organizationOperatorRetrieverService;
    @Value("${app.enable-access-organization-mode}")
    private boolean organizationAccessMode;


    public UserServiceImpl(TokenStoreService tokenStoreService, UserRegistrationService userRegistrationService, OperatorRegistrationService operatorRegistrationService, IamUserInfoDTO2UserInfoMapper userInfoMapper, OrganizationOperatorRetrieverService organizationOperatorRetrieverService) {
        this.tokenStoreService = tokenStoreService;
        this.userRegistrationService = userRegistrationService;
        this.operatorRegistrationService = operatorRegistrationService;
        this.userInfoMapper = userInfoMapper;
        this.organizationOperatorRetrieverService = organizationOperatorRetrieverService;
    }

    @Override
    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName) {
        return userRegistrationService.registerUser(externalUserId, fiscalCode, iamIssuer, firstName, lastName);
    }

    @Override
    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles, String email) {
        return operatorRegistrationService.registerOperator(userId, organizationIpaCode, roles, email);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        log.debug("Retrieving user info");
        IamUserInfoDTO userInfo = tokenStoreService.load(accessToken);
        if (userInfo == null) {
            throw new InvalidAccessTokenException("AccessToken not found");
        }

        EntityModelBroker brokerInfo = null;

        if (Boolean.TRUE.equals(organizationAccessMode) && userInfo.getOrganizationAccess() != null) {
            log.debug("SelfCare mode enabled. Using organizationAccess: {}", userInfo.getOrganizationAccess());

            String organizationIpaCode = userInfo.getOrganizationAccess().getOrganizationIpaCode();
            if (organizationIpaCode != null) {
                EntityModelOrganization organization = organizationClient.getOrganizationByIpaCode(organizationIpaCode, accessToken);

                if (organization != null && organization.getBrokerId() != null) {
                    log.info("Organization found. Fetching broker details for brokerId: {}", organization.getBrokerId());
                    brokerInfo = organizationClient.getBrokerById(organization.getBrokerId(), accessToken);
                } else {
                    log.warn("No valid organization or brokerId found for IPA Code: {}", organizationIpaCode);
                }
            }
        } else {
            log.debug("SelfCare mode disabled or organizationAccess not provided. Cannot fetch organization.");
        }

        UserInfo result = userInfoMapper.apply(userInfo);
        if (brokerInfo != null) {
            result.setBrokerId(brokerInfo.getBrokerId());
            result.setBrokerFiscalCode(brokerInfo.getBrokerFiscalCode());
        }

        log.debug("User info retrieved successfully with brokerId: {}",
                brokerInfo != null ? brokerInfo.getBrokerId() : "N/A");
        return result;
    }

    @Override
    public Page<OperatorDTO> retrieveOrganizationOperators(String organizationIpaCode, Pageable pageable) {
        log.info("Retrieving organization {} operators", organizationIpaCode);
        return organizationOperatorRetrieverService.retrieveOrganizationOperators(organizationIpaCode, pageable);
    }

}
