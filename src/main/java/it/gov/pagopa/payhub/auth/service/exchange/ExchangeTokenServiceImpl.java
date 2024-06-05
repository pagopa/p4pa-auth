package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ExchangeTokenServiceImpl implements ExchangeTokenService{

    private final ValidateExternalTokenService validateExternalTokenService;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final TokenStoreService tokenStoreService;
    private final IDTokenClaims2UserInfoMapper idTokenClaimsMapper;

    public ExchangeTokenServiceImpl(ValidateExternalTokenService validateExternalTokenService, AccessTokenBuilderService accessTokenBuilderService, TokenStoreService tokenStoreService, IDTokenClaims2UserInfoMapper idTokenClaimsMapper) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.tokenStoreService = tokenStoreService;
        this.idTokenClaimsMapper = idTokenClaimsMapper;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        log.info("Client {} requested to exchange a {} token provided by {} asking for grant type {} and scope {}",
                clientId, subjectTokenType, subjectIssuer, grantType, scope);
        Map<String, Claim> claims = validateExternalTokenService.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        AccessToken accessToken = accessTokenBuilderService.build();
        tokenStoreService.save(accessToken.getAccessToken(), idTokenClaimsMapper.apply(claims));
        return accessToken;
    }
}
