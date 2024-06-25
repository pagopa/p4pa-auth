package it.gov.pagopa.payhub.auth.service.user.registration;

import org.springframework.stereotype.Service;

@Service
public class FiscalCodeObfuscatorService {
    public String obfuscate(String fiscalCode){
        return fiscalCode;
    }
}
