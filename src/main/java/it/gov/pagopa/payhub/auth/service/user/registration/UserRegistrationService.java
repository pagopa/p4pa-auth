package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.mypay.service.MyPayUsersService;
import it.gov.pagopa.payhub.auth.mypivot.service.MyPivotUsersService;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserRegistrationService {

    private final ExternalUserIdObfuscatorService externalUserIdObfuscatorService;
    private final FiscalCodeObfuscatorService fiscalCodeObfuscatorService;
    private final UsersRepository usersRepository;
    private final MyPayUsersService myPayUsersService;
    private final MyPivotUsersService myPivotUsersService;

    public UserRegistrationService(ExternalUserIdObfuscatorService externalUserIdObfuscatorService,
        FiscalCodeObfuscatorService fiscalCodeObfuscatorService, UsersRepository usersRepository,
        MyPayUsersService myPayUsersService, MyPivotUsersService myPivotUsersService) {
        this.externalUserIdObfuscatorService = externalUserIdObfuscatorService;
        this.fiscalCodeObfuscatorService = fiscalCodeObfuscatorService;
        this.usersRepository = usersRepository;
        this.myPayUsersService = myPayUsersService;
        this.myPivotUsersService = myPivotUsersService;
    }

    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName){
        User user = buildUser(externalUserId, fiscalCode, iamIssuer, firstName, lastName);
        log.info("Registering user having mappedExternalUserId {}", user.getMappedExternalUserId());

        myPayUsersService.registerMyPayUser(user.getMappedExternalUserId());
        log.info("Registering user on MyPay having mappedExternalUserId {}", externalUserId);

        myPivotUsersService.registerMyPivotUser(user.getMappedExternalUserId(), fiscalCode, firstName, lastName);
        log.info("Registering user on MyPivot having mappedExternalUserId {}", externalUserId);

        return usersRepository.registerUser(user);
    }

    private User buildUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName){
        return User.builder()
                .iamIssuer(iamIssuer)
                .mappedExternalUserId(externalUserIdObfuscatorService.obfuscate(externalUserId))
                .userCode(fiscalCodeObfuscatorService.obfuscate(fiscalCode))
                .fiscalCode(fiscalCode)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
