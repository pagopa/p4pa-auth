package it.gov.pagopa.payhub.auth.service.exchange;

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

    public ExchangeTokenServiceImpl(ValidateExternalTokenService validateExternalTokenService, AccessTokenBuilderService accessTokenBuilderService, TokenStoreService tokenStoreService) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.tokenStoreService = tokenStoreService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        log.info("Client {} requested to exchange a {} token provided by {} asking for grant type {} and scope {}",
                clientId, subjectTokenType, subjectIssuer, grantType, scope);
        Map<String, String> claims = validateExternalTokenService.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        AccessToken accessToken = accessTokenBuilderService.build();
        tokenStoreService.save(accessToken.getAccessToken(), claims);
        return accessToken;
    }
}
