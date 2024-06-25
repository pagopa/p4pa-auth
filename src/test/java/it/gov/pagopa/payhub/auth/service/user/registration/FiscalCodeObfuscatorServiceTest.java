package it.gov.pagopa.payhub.auth.service.user.registration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiscalCodeObfuscatorServiceTest {

    private FiscalCodeObfuscatorService service;

    @BeforeEach
    void init(){
        service = new FiscalCodeObfuscatorService();
    }
    
    @Test
    void whenObfuscateThenOk(){
        //Given
        String fiscalCode = "FISCALCODE";

        // When
        String result = service.obfuscate(fiscalCode);

        // Then
        Assertions.assertSame(fiscalCode, result);
    }
}
