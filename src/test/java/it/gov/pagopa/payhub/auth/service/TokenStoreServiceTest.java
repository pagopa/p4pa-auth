package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenStoreServiceTest {

    private final TokenStoreService service = new TokenStoreServiceImpl();

    @Test
    void givenClaimsWhenSaveThenReturnThem(){
        // Given
        UserInfo userInfo = new UserInfo();
        String accessToken = "AccessToken";

        // When
        UserInfo result = service.save(accessToken, userInfo);

        // Then
        Assertions.assertSame(userInfo, result);
    }

    @Test
    void givenAccessTokenWhenLoadThenNull(){
        // Given
        String accessToken = "AccessToken";

        // When
        UserInfo result = service.load(accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
