package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.user.registration.UserRegistrationService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final TokenStoreService tokenStoreService;
    private final UserRegistrationService userRegistrationService;

    public UserServiceImpl(TokenStoreService tokenStoreService, UserRegistrationService userRegistrationService) {
        this.tokenStoreService = tokenStoreService;
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer) {
        return userRegistrationService.registerUser(externalUserId, fiscalCode, iamIssuer);
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        UserInfo userInfo = tokenStoreService.load(accessToken);
        if(userInfo==null){
            throw new InvalidAccessTokenException("AccessToken not found");
        } else {
            return userInfo;
        }
    }
}
