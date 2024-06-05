package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final TokenStoreService tokenStoreService;
    public UserServiceImpl(TokenStoreService tokenStoreService) {
        this.tokenStoreService = tokenStoreService;
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
