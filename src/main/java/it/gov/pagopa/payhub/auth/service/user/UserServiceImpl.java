package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.OperatorRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OrganizationOperatorRetrieverService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.UserInfoRetrieverService;
import it.gov.pagopa.payhub.auth.utils.ErrorCodeConstants;
import it.gov.pagopa.payhub.dto.generated.OperatorDTO;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import it.gov.pagopa.payhub.dto.generated.UserInfoLimitedScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final TokenStoreService tokenStoreService;
    private final UserRegistrationService userRegistrationService;
    private final OperatorRegistrationService operatorRegistrationService;
    private final IamUserInfoDTO2UserInfoMapper userInfoMapper;
    private final OrganizationOperatorRetrieverService organizationOperatorRetrieverService;
    private final UserInfoRetrieverService userInfoRetrieverService;


    public UserServiceImpl(TokenStoreService tokenStoreService, UserRegistrationService userRegistrationService, OperatorRegistrationService operatorRegistrationService, IamUserInfoDTO2UserInfoMapper userInfoMapper, OrganizationOperatorRetrieverService organizationOperatorRetrieverService, UserInfoRetrieverService userInfoRetrieverService) {
        this.tokenStoreService = tokenStoreService;
        this.userRegistrationService = userRegistrationService;
        this.operatorRegistrationService = operatorRegistrationService;
        this.userInfoMapper = userInfoMapper;
        this.organizationOperatorRetrieverService = organizationOperatorRetrieverService;
        this.userInfoRetrieverService = userInfoRetrieverService;
    }

    @Override
    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName) {
        return userRegistrationService.registerUser(externalUserId, fiscalCode, iamIssuer, firstName, lastName);
    }

    @Override
    public Operator registerOperator(User user, String organizationIpaCode, Set<String> roles, String email, String accessToken) {
        return operatorRegistrationService.registerOperator(user, organizationIpaCode, roles, email, accessToken);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        log.debug("Retrieving user info");
        IamUserInfoDTO userInfo = tokenStoreService.load(accessToken);

        if (userInfo == null) {
            throw new InvalidAccessTokenException(ErrorCodeConstants.ERROR_CODE_INVALID_TOKEN, "AccessToken not found");
        }

        UserInfo result = userInfoMapper.apply(userInfo, accessToken);

        if (result instanceof UserInfoLimitedScope resultScoped && Boolean.TRUE.equals(resultScoped.getResource().getSingleUsage())) {
            tokenStoreService.delete(accessToken);
        }

        log.debug("User info retrieved successfully with brokerId: {}", result.getBrokerId());

        return result;
    }

    @Override
    public UserInfo getUserInfoFromMappedExternalUserId(String mappedExternalUserId, String accessToken) {
        return userInfoRetrieverService.findByMappedExternalUserId(mappedExternalUserId, accessToken);
    }

    @Override
    public Page<OperatorDTO> retrieveOrganizationOperators(String organizationIpaCode, Pageable pageable) {
        log.info("Retrieving organization {} operators", organizationIpaCode);
        return organizationOperatorRetrieverService.retrieveOrganizationOperators(organizationIpaCode, pageable);
    }

}
