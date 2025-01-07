package it.gov.pagopa.payhub.auth.controller;

import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderServiceTest;
import it.gov.pagopa.payhub.auth.utils.CertUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ExtendWith(MockitoExtension.class)
class WellKnownControllerTest {

    @Mock
    private AccessTokenBuilderService accessTokenBuilderServiceMock;

    private WellKnownController controller;

    @BeforeEach
    void init() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        PublicJwk<?> jwk = Jwks.builder().id("KID").key(CertUtils.pemPub2PublicKey(AccessTokenBuilderServiceTest.PUBLIC_KEY)).build();
        Mockito.doReturn(jwk)
                .when(accessTokenBuilderServiceMock).getJwk();

        controller = new WellKnownController(accessTokenBuilderServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(accessTokenBuilderServiceMock);
    }

    @Test
    void whenGetJwksThenReturnConstantString(){
        ResponseEntity<String> result = controller.jwks();

        Assertions.assertEquals("{\"keys\":[{\"kid\":\"KID\",\"kty\":\"RSA\",\"n\":\"2ovm_rd3g69dq9PisinQ6mWy8ZttT8D-GKXCsHZycsGnN7b74TPyYy-4-h-9cgJeizp8RDRrufHjiBrqi_2reOk_rD7ZHbpfQvHK8MYfgIVdtTxYMX_GGdOrX6_5TV2b8e2aCG6GmxF0UuEvxY9oTmcZUxnIeDtl_ixz4DQ754eS363qWfEA92opW-jcYzr07sbQtR86e-Z_s_CUeX6W1PHNvBqdlAgp2ecr_1DOLq1D9hEANBPSwbt-FM6FNe4vLphi7GTwiB0yaAuy-jE8odND6HPvvvmgbK1_2qTHn_HJjWUm11LUC73BszR32BKbdEEhxPQnnwswVekWzPi1Iw\",\"e\":\"AQAB\"}]}"
                , result.getBody());
    }
}
