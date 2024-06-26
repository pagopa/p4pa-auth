package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.OperatorRegistrationService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService{

    private final TokenStoreService tokenStoreService;
    private final UserRegistrationService userRegistrationService;
    private final OperatorRegistrationService operatorRegistrationService;
    private final IamUserInfoDTO2UserInfoMapper userInfoMapper;

    public UserServiceImpl(TokenStoreService tokenStoreService, UserRegistrationService userRegistrationService, OperatorRegistrationService operatorRegistrationService, IamUserInfoDTO2UserInfoMapper userInfoMapper) {
        this.tokenStoreService = tokenStoreService;
        this.userRegistrationService = userRegistrationService;
        this.operatorRegistrationService = operatorRegistrationService;
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer) {
        return userRegistrationService.registerUser(externalUserId, fiscalCode, iamIssuer);
    }

    @Override
    public Operator registerOperator(String userId, String organizationIpaCode, Set<String> roles) {
        return operatorRegistrationService.registerOperator(userId, organizationIpaCode, roles);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        IamUserInfoDTO userInfo = tokenStoreService.load(accessToken);
        if(userInfo==null){
            throw new InvalidAccessTokenException("AccessToken not found");
        } else {
            return userInfoMapper.apply(userInfo);
        }
    }
}
