package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.user.registration.ExternalUserIdObfuscatorService;
import org.springframework.stereotype.Service;

@Service
public class FakeUserInfoService {

    private final UsersRepository usersRepository;
    private final ExternalUserIdObfuscatorService externalUserIdObfuscatorService;

    public FakeUserInfoService(UsersRepository usersRepository, ExternalUserIdObfuscatorService externalUserIdObfuscatorService) {
        this.usersRepository = usersRepository;
        this.externalUserIdObfuscatorService = externalUserIdObfuscatorService;
    }

    public IamUserInfoDTO buildIamUserInfoFake(String iamUserId, String subjectIssuer) {
        String mappedExternalUserId = externalUserIdObfuscatorService.obfuscate(iamUserId);
        User userInfo = usersRepository.findByMappedExternalUserId(mappedExternalUserId)
                .orElseThrow(() -> new UserNotFoundException("User with this mappedExternalUserId not found"));
        return IamUserInfoDTO.builder()
                .userId(iamUserId)
                .innerUserId(userInfo.getUserId())
                .name("fake")
                .familyName("user")
                .fiscalCode(userInfo.getUserCode())
                .issuer(subjectIssuer)
                .build();
    }
}
