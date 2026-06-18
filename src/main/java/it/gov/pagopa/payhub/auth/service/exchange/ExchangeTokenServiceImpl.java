package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ExchangeTokenServiceImpl implements ExchangeTokenService {

    public static final String SUBJECT_TOKEN_TYPE_FAKE = "FAKE-AUTH";

    private static final Set<String> FAKE_AUTH_ENABLED_ENVS = Set.of("DEV", "UAT");

    private static final String TECHNICAL_EXTERNAL_USER_ID = "piattaforma-unitaria";

    private String technicalAccessToken;

    private final String env;
    private final ValidateExternalTokenService validateExternalTokenService;
    private final AccessTokenBuilderService accessTokenBuilderService;
    private final TokenStoreService tokenStoreService;
    private final IDTokenClaims2UserInfoMapper idTokenClaimsMapper;
    private final IamUserRegistrationService iamUserRegistrationService;
    private final FakeUserInfoService fakeUserInfoService;

    public ExchangeTokenServiceImpl(
            @Value("${app.env}") String env,
            ValidateExternalTokenService validateExternalTokenService,
            AccessTokenBuilderService accessTokenBuilderService,
            TokenStoreService tokenStoreService,
            IDTokenClaims2UserInfoMapper idTokenClaimsMapper,
            IamUserRegistrationService iamUserRegistrationService, FakeUserInfoService fakeUserInfoService) {
        this.env = env;
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
        if (FAKE_AUTH_ENABLED_ENVS.contains(env) && SUBJECT_TOKEN_TYPE_FAKE.equals(subjectTokenType)) {
            return handleFakeAuth(subjectToken, subjectIssuer);
        }
        Map<String, Claim> claims = validateExternalTokenService.validate(clientId, subjectToken, subjectIssuer, subjectTokenType, scope);
        IamUserInfoDTO iamUser = idTokenClaimsMapper.apply(claims);
        User registeredUser = iamUserRegistrationService.registerUser(iamUser, getTechnicalAccessToken(iamUser));
        MDC.put("externalUserId", registeredUser.getMappedExternalUserId());
        iamUser.setInnerUserId(registeredUser.getUserId());
        iamUser.setMappedExternalUserId(registeredUser.getMappedExternalUserId());

        AccessToken accessToken = accessTokenBuilderService.build(iamUser);
        tokenStoreService.save(accessToken.getAccessToken(), iamUser);
        return accessToken;
    }

    private String getTechnicalAccessToken(IamUserInfoDTO iamUser) {
        if(technicalAccessToken != null && Instant.now().isBefore(JWT.decode(technicalAccessToken).getExpiresAt().toInstant())) {
            return technicalAccessToken;
        }
        iamUser.setMappedExternalUserId(TECHNICAL_EXTERNAL_USER_ID);
        technicalAccessToken = accessTokenBuilderService.build(iamUser).getAccessToken();
        return technicalAccessToken;
    }

    private AccessToken handleFakeAuth(String iamUserId, String subjectIssuer) {
        IamUserInfoDTO fakeIamUserInfo = fakeUserInfoService.buildIamUserInfoFake(iamUserId, subjectIssuer);
        AccessToken accessToken = accessTokenBuilderService.build(fakeIamUserInfo);
        tokenStoreService.save(accessToken.getAccessToken(), fakeIamUserInfo);
        return accessToken;
    }
}
