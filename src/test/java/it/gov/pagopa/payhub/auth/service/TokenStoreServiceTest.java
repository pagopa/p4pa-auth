package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenStoreServiceTest {

    private final TokenStoreService service = new TokenStoreServiceImpl();

    @Test
    void givenClaimsWhenSaveThenReturnThem(){
        // Given
        IamUserInfoDTO userInfo = new IamUserInfoDTO();
        String accessToken = "AccessToken";

        // When
        IamUserInfoDTO result = service.save(accessToken, userInfo);

        // Then
        Assertions.assertSame(userInfo, result);
    }

    @Test
    void givenAccessTokenWhenLoadThenNull(){
        // Given
        String accessToken = "AccessToken";

        // When
        IamUserInfoDTO result = service.load(accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
