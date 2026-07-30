package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.connector.debtposition.DebtPositionTypeOrgOperatorService;
import it.gov.pagopa.payhub.auth.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.pu.p4pa_organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class OperatorRegistrationService {

    private final OperatorsRepository operatorsRepository;
    private final OrganizationService organizationService;
    private final DebtPositionTypeOrgOperatorService debtPositionTypeOrgOperatorService;

    public OperatorRegistrationService(OperatorsRepository operatorsRepository,
                                       OrganizationService organizationService,
                                       DebtPositionTypeOrgOperatorService debtPositionTypeOrgOperatorService) {
        this.operatorsRepository = operatorsRepository;
        this.organizationService = organizationService;
        this.debtPositionTypeOrgOperatorService = debtPositionTypeOrgOperatorService;
    }

    public Operator registerOperator(User user, String organizationIpaCode, Set<String> roles, String email, String externalOrganizationId, String accessToken){
        log.info("Registering relationship between userId {} and organization {} setting roles {}",
                user.getUserId(), organizationIpaCode, roles);
        Operator operator = operatorsRepository.registerOperator(user.getUserId(), organizationIpaCode, email, roles);
        Organization organization = organizationService.getOrganizationByIpaCode(organizationIpaCode, accessToken);
        if(organization == null) {
            log.info("Skipping saving of default technical DPTypeOrgs for user with userId {} and organization {}",
                    user.getUserId(), organizationIpaCode);
            return operator;
        }
        debtPositionTypeOrgOperatorService.saveDefaultTechnicalDebtPositionTypeOrgForOperator(
                user.getMappedExternalUserId(),
                organization.getOrganizationId(),
                accessToken
        );
        updateOrganizationExternalId(organization.getOrganizationId(), organization.getExternalOrganizationId(), externalOrganizationId, accessToken);
        return operator;
    }

    private void updateOrganizationExternalId(Long organizationId, String actualExternalOrganizationId, String externalOrganizationId, String accessToken) {
        if (externalOrganizationId != null && !externalOrganizationId.equals(actualExternalOrganizationId)) {
            organizationService.updateExternalOrganizationId(organizationId, externalOrganizationId, accessToken);
        }
    }

}
