package it.gov.pagopa.payhub.auth.service.a2a.legacy;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.mapper.A2ALegacyClaims2UserInfoMapper;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class JWTLegacyHandlerService {
	private final ValidateJWTLegacyService validateJWTLegacyService;
	private final A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapper;

	public JWTLegacyHandlerService(ValidateJWTLegacyService validateJWTLegacyService, A2ALegacyClaims2UserInfoMapper a2ALegacyClaims2UserInfoMapper) {
		this.validateJWTLegacyService = validateJWTLegacyService;
		this.a2ALegacyClaims2UserInfoMapper = a2ALegacyClaims2UserInfoMapper;
	}

	public UserInfo handleLegacyToken(String token) {
		Pair<String, Map<String, Claim>> claims = validateJWTLegacyService.validate(token);
		return a2ALegacyClaims2UserInfoMapper.map(claims.getLeft());
	}
}
