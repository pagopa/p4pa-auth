package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.mapper.A2ALegacyClaims2UserInfoMapper;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class JWTLegacyHandlerServiceTest {
	@Mock
	private ValidateJWTLegacyService validateJWTLegacyServiceMock;
	@Mock
	private TokenStoreService tokenStoreServiceMock;
	@Mock
	private A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapperMock;

	private JWTLegacyHandlerService service;

	@BeforeEach
	void init() { service = new JWTLegacyHandlerService(validateJWTLegacyServiceMock, tokenStoreServiceMock, a2ALegacyClaims2UserInfoMapperMock);	}

	@Test
	void givenValidTokenWhenHandleLegacyTokenThenSuccess() {
		//Given
		String token = "accessToken";//"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAplNu5dm2xSvbfa+guCFMMw7eKDZV/Z5k9XtW19/0dWGFLCCpGDIvOaOhdCMC/wH/ESyVmbaYzvU+ehRtqUNIF4zhitjY5+5atqiku/GBYp0x9tYLnS2Ztr9dVyW/kMA0s1gd5fSH/5hD44M2iKXSLut/IfDlWD4KAZBmaU/0q1iaMfdtG8yt11UEOQh9Ia0+BFv4Iy7tHY+bckTPVdIrLKlpG9EHPGUm+LNx9dH+SML0fhCGO0bSInb8UdGGwikxSr/CYi3+Kzsfox6wW31SSqkqrhI5u9gmttimtRAE0ordqNpe+8JJMY5ZOVcpLvAb1vtu3xKtwiDAUBAWPFyGCjDfVIiQJu1qI0kMd8J/eV6sz42PVUJyqQi/+T/Qpqy7YCGYVx5Q6k5zhTWRRti/wvSU5Je0nmzydhZUy4vT8WyWyQvNUvjlswEXFDiUbw4x7pSKp10ec0c9md24/zAHHyU/rgeiJEdK1KHN+rzE+BJH7ru4HNW/ZF+0IPjlQ8sm3MisuTJzq8uedYFCDRfR9P1u7jee1OyNwyw6Iy06cK0TIl1xFcqKKvR9KxLBwMINrSM3BcE0sDpr5qc7wthJSe0zC/XbNkb72UtWoI7rPox7WY/DvuScK5uKPhgUQTZQeBfWIN7/Kpqra7tcrw14RdexxO1QOFRlPGy+FfdOyU0CAwEAAQ==";
		Pair<String, Map<String, Claim>> immutablePairClaims = createJWKClaims();
		Mockito.when(validateJWTLegacyServiceMock.validate(token)).thenReturn(immutablePairClaims);

		AccessToken expectedAccessToken = AccessToken.builder().accessToken(token).build();
		IamUserInfoDTO iamUserInfo = new IamUserInfoDTO();
		Mockito.when(a2ALegacyClaims2UserInfoMapperMock.map("subject")).thenReturn(iamUserInfo);
		//When
		service.handleLegacyToken(token);
		//Then
		Mockito.verify(tokenStoreServiceMock).save(Mockito.same(expectedAccessToken.getAccessToken()), Mockito.same(iamUserInfo));
		Assertions.assertDoesNotThrow(() -> service.handleLegacyToken(token));
	}

	private Pair<String, Map<String, Claim>>  createJWKClaims(){
		Map<String, Claim> decoded = JWT.decode(JWT.create()
			.withClaim("type", "a2a")
			.withIssuer("iss")
			.withAudience("aud")
			.withIssuedAt(Instant.now())
			.withExpiresAt(Instant.ofEpochSecond(1715267318))
			.withJWTId("my-key-id")
			.sign(Algorithm.none())).getClaims();
		return new ImmutablePair<>("subject", decoded);
	}
}
