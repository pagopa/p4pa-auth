package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.model.User;
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
    private final IamUserRegistrationService iamUserRegistrationService;
    public static final String SUBJECT_TOKEN_TYPE_TEST="urn:ietf:params:oauth:token-type:jwt-test";

    public ExchangeTokenServiceImpl(
            ValidateExternalTokenService validateExternalTokenService,
            AccessTokenBuilderService accessTokenBuilderService,
            TokenStoreService tokenStoreService,
            IDTokenClaims2UserInfoMapper idTokenClaimsMapper,
            IamUserRegistrationService iamUserRegistrationService) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.tokenStoreService = tokenStoreService;
        this.idTokenClaimsMapper = idTokenClaimsMapper;
        this.iamUserRegistrationService = iamUserRegistrationService;
    }

    @Override
    public AccessToken postToken(String clientId, String grantType, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        log.info("Client {} requested to exchange a {} token provided by {} asking for grant type {} and scope {}",
                clientId, subjectTokenType, subjectIssuer, grantType, scope);
        if(SUBJECT_TOKEN_TYPE_TEST.equals(subjectTokenType)){
            AccessToken accessToken = accessTokenBuilderService.build();
            IamUserInfoDTO iamUser = idTokenClaimsMapper.buildIamUserTestInfo(subjectToken, subjectIssuer);
            tokenStoreService.save(accessToken.getAccessToken(), iamUser);
            return accessToken;
        }
        Map<String, Claim> claims = validateExternalTokenService.validate(clientId, grantType, subjectToken, subjectIssuer, subjectTokenType, scope);
        AccessToken accessToken = accessTokenBuilderService.build();
        IamUserInfoDTO iamUser = idTokenClaimsMapper.apply(claims);
        User registeredUser = iamUserRegistrationService.registerUser(iamUser);
        iamUser.setInnerUserId(registeredUser.getUserId());
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);
        return accessToken;
    }
}
