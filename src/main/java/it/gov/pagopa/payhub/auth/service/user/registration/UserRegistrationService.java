package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserRegistrationService {

    private final ExternalUserIdObfuscatorService externalUserIdObfuscatorService;
    private final FiscalCodeObfuscatorService fiscalCodeObfuscatorService;
    private final UsersRepository usersRepository;

    public UserRegistrationService(ExternalUserIdObfuscatorService externalUserIdObfuscatorService, FiscalCodeObfuscatorService fiscalCodeObfuscatorService, UsersRepository usersRepository) {
        this.externalUserIdObfuscatorService = externalUserIdObfuscatorService;
        this.fiscalCodeObfuscatorService = fiscalCodeObfuscatorService;
        this.usersRepository = usersRepository;
    }

    public User registerUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName, String email){
        User user = buildUser(externalUserId, fiscalCode, iamIssuer, firstName, lastName, email);
        log.info("Registering user having mappedExternalUserId {}", user.getMappedExternalUserId());
        return usersRepository.registerUser(user);
    }

    private User buildUser(String externalUserId, String fiscalCode, String iamIssuer, String firstName, String lastName, String email){
        return User.builder()
                .iamIssuer(iamIssuer)
                .mappedExternalUserId(externalUserIdObfuscatorService.obfuscate(externalUserId))
                .userCode(fiscalCodeObfuscatorService.obfuscate(fiscalCode))

                .fiscalCode(fiscalCode)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)

                .build();
    }
}
