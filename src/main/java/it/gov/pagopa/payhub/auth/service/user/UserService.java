package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    User registerUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName);
    Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles, String mappedExternalUserId, String email);
    UserInfo getUserInfo(String accessToken);
    Page<OperatorDTO> retrieveOrganizationOperators(String organizationIpaCode, Pageable pageable);
}
