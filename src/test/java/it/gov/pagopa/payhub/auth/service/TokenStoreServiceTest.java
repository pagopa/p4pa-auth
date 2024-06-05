package it.gov.pagopa.payhub.auth.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TokenStoreServiceTest {

    private final TokenStoreService service = new TokenStoreServiceImpl();

    @Test
    void givenClaimsWhenSaveThenReturnThem(){
        // Given
        HashMap<String, String> idTokenClaims = new HashMap<>();
        String accessToken = "AccessToken";

        // When
        Map<String, String> result = service.save(accessToken, idTokenClaims);

        // Then
        Assertions.assertSame(idTokenClaims, result);
    }

    @Test
    void givenAccessTokenWhenSaveThenNull(){
        // Given
        String accessToken = "AccessToken";

        // When
        Map<String, String> result = service.load(accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
