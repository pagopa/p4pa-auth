package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Override
    public UserInfo getUserInfo(String accessToken) {
        return null; //TODO
    }
}
