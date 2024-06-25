package it.gov.pagopa.payhub.auth.service.user.registration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExternalUserIdObfuscatorServiceTest {

    private ExternalUserIdObfuscatorService service;

    @BeforeEach
    void init(){
        service = new ExternalUserIdObfuscatorService();
    }

    @Test
    void whenObfuscateThenOk(){
        //Given
        String externalUserId = "EXTERNALUSERID";

        // When
        String result = service.obfuscate(externalUserId);

        // Then
        Assertions.assertSame(externalUserId, result);
    }
}
