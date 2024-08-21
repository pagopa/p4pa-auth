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
    void givenShortStringWhenObfuscateThenOk(){
        //Given
        String fiscalCode = "A";

        // When
        String result = service.obfuscate(fiscalCode);

        // Then
        Assertions.assertEquals(fiscalCode, result);
    }

    @Test
    void givenCompleteCfWhenObfuscateThenOk(){
        //Given
        String fiscalCode = "AAAAAA00A00A000A";

        // When
        String result = service.obfuscate(fiscalCode);

        // Then
        Assertions.assertEquals("AXAAXA0XA00XXXXX", result);
    }
}
