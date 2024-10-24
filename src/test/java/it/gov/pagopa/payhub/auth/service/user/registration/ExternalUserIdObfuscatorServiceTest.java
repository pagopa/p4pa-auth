package it.gov.pagopa.payhub.auth.service.user.registration;

import it.gov.pagopa.payhub.auth.service.DataCipherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

@ExtendWith(MockitoExtension.class)
class ExternalUserIdObfuscatorServiceTest {

    @Mock
    private DataCipherService dataCipherService;

    @InjectMocks
    private ExternalUserIdObfuscatorService service;

    @Test
    void whenObfuscateThenOk(){
        //Given
        String externalUserId = "EXTERNALUSERID";
        byte[] hashExpected = new byte[0];

        // When
        Mockito.when(dataCipherService.hash(externalUserId)).thenReturn(hashExpected);
        String result = service.obfuscate(externalUserId);

        // Then
        Assertions.assertEquals(Base64.getUrlEncoder().encodeToString(hashExpected), result);
    }
}
