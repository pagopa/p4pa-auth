package it.gov.pagopa.payhub.auth.service.user.registration;

import org.springframework.stereotype.Service;

@Service
public class ExternalUserIdObfuscatorService {
    public String obfuscate(String externalUserId){
        return externalUserId;
    }
}
