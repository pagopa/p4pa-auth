package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.mapper.A2ALegacyClaims2UserInfoMapper;
import it.gov.pagopa.payhub.model.generated.UserInfo;
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
	private A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapperMock;

	private JWTLegacyHandlerService service;

	@BeforeEach
	void init() { service = new JWTLegacyHandlerService(validateJWTLegacyServiceMock, a2ALegacyClaims2UserInfoMapperMock);	}

	@Test
	void givenValidTokenWhenHandleLegacyTokenThenSuccess() {
		//Given
		String token = "accessToken";
		Pair<String, Map<String, Claim>> immutablePairClaims = createJWKClaims();
		Mockito.when(validateJWTLegacyServiceMock.validate(token)).thenReturn(immutablePairClaims);

		UserInfo userInfo = new UserInfo();
		Mockito.when(a2ALegacyClaims2UserInfoMapperMock.map("subject")).thenReturn(userInfo);
		//When
		UserInfo result = service.handleLegacyToken(token);
		//Then
		Assertions.assertEquals(userInfo, result);
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
