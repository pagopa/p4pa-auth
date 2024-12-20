package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    private final FakeUserInfoService fakeUserInfoService;
    public static final String SUBJECT_TOKEN_TYPE_FAKE = "FAKE-AUTH";

    public ExchangeTokenServiceImpl(
            ValidateExternalTokenService validateExternalTokenService,
            AccessTokenBuilderService accessTokenBuilderService,
            TokenStoreService tokenStoreService,
            IDTokenClaims2UserInfoMapper idTokenClaimsMapper,
            IamUserRegistrationService iamUserRegistrationService, FakeUserInfoService fakeUserInfoService) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.accessTokenBuilderService = accessTokenBuilderService;
        this.tokenStoreService = tokenStoreService;
        this.idTokenClaimsMapper = idTokenClaimsMapper;
        this.iamUserRegistrationService = iamUserRegistrationService;
        this.fakeUserInfoService = fakeUserInfoService;
    }

    @Override
    public AccessToken postToken(String clientId, String subjectToken, String subjectIssuer, String subjectTokenType, String scope) {
        log.info("Client {} requested to exchange a {} token provided by {} asking for token-exchange grant type and scope {}",
                clientId, subjectTokenType, subjectIssuer, scope);
        if(SUBJECT_TOKEN_TYPE_FAKE.equals(subjectTokenType)){
            return handleFakeAuth(subjectToken, subjectIssuer);
        }
        Map<String, Claim> claims = validateExternalTokenService.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);
        AccessToken accessToken = accessTokenBuilderService.build();
        IamUserInfoDTO iamUser = idTokenClaimsMapper.apply(claims);
        User registeredUser = iamUserRegistrationService.registerUser(iamUser);
        MDC.put("externalUserId", registeredUser.getMappedExternalUserId());
        iamUser.setInnerUserId(registeredUser.getUserId());
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);
        return accessToken;
    }

    private AccessToken handleFakeAuth(String iamUserId, String subjectIssuer){
        AccessToken accessToken = accessTokenBuilderService.build();
        IamUserInfoDTO iamUser = fakeUserInfoService.buildIamUserInfoFake(iamUserId, subjectIssuer);
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);
        return accessToken;
    }
}
