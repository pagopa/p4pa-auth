package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.RegisteredClaims;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.mapper.A2ALegacyClaims2UserInfoMapper;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class JWTLegacyHandlerService {
	private final ValidateJWTLegacyService validateJWTLegacyService;
	private final TokenStoreService tokenStoreService;
	private final A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapper;

	public JWTLegacyHandlerService(ValidateJWTLegacyService validateJWTLegacyService, TokenStoreService tokenStoreService, A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapper) {
		this.validateJWTLegacyService = validateJWTLegacyService;
		this.tokenStoreService = tokenStoreService;
		this.a2ALegacyClaims2UserInfoMapper = a2ALegacyClaims2UserInfoMapper;
	}

	public void handleLegacyToken(String token) {
		Pair<String, Map<String, Claim>> claims = validateJWTLegacyService.validate(token);
		AccessToken accessToken = AccessToken.builder()
			.accessToken(token)
			.tokenType("bearer")
			.expiresIn(claims.getRight().get(RegisteredClaims.EXPIRES_AT).asInt())
			.build();
		IamUserInfoDTO iamUser = a2ALegacyClaims2UserInfoMapper.map(claims.getLeft());
		tokenStoreService.save(accessToken.getAccessToken(), iamUser);
	}
}
