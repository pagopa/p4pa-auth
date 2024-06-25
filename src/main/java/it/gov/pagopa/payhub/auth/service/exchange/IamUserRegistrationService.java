package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IamUserRegistrationService {

    private final boolean organizationAccessMode;

    private final UserService userService;

    public IamUserRegistrationService(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode,

            UserService userService
            ) {
        this.organizationAccessMode = organizationAccessMode;
        this.userService = userService;
    }

    void registerUser(UserInfo userInfo){
        userService.registerUser(userInfo.getUserId(), userInfo.getFiscalCode(), userInfo.getIssuer());

        if(organizationAccessMode){
            //store Operators
        }
    }
}
