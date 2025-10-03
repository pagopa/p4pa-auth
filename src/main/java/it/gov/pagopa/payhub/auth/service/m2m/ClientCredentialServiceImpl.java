package it.gov.pagopa.payhub.auth.service.m2m;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.mapper.Client2UserInfoMapper;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientCredentialServiceImpl implements ClientCredentialService {

	private final ValidateClientCredentialsService validateClientCredentialsService;
	private final AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService;
	private final AccessTokenBuilderService accessTokenBuilderService;
	private final TokenStoreService tokenStoreService;
	private final Client2UserInfoMapper client2UserInfoMapper;
	private final AuditLoggerService auditService;

	public ClientCredentialServiceImpl(
			ValidateClientCredentialsService validateClientCredentialsService,
			AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService,
			AccessTokenBuilderService accessTokenBuilderService,
			TokenStoreService tokenStoreService, Client2UserInfoMapper client2UserInfoMapper,
      AuditLoggerService auditService) {
		this.validateClientCredentialsService = validateClientCredentialsService;
		this.authorizeClientCredentialsRequestService = authorizeClientCredentialsRequestService;
		this.accessTokenBuilderService = accessTokenBuilderService;
		this.tokenStoreService = tokenStoreService;
		this.client2UserInfoMapper = client2UserInfoMapper;
    this.auditService = auditService;
  }

	@Override
	public AccessToken postToken(String clientId, String scope, String clientSecret) {
		log.info("Client {} requested authentication with client_credentials grant type and scope {}", clientId, scope);
		validateClientCredentialsService.validate(scope, clientSecret);
		ClientNoSecretDTO authorizedClient = authorizeClientCredentialsRequestService.authorizeCredentials(clientId, clientSecret);
		IamUserInfoDTO iamUser = client2UserInfoMapper.apply(authorizedClient);
		AccessToken accessToken = accessTokenBuilderService.build(iamUser);
		MDC.put("externalUserId", iamUser.getUserId());
		tokenStoreService.save(accessToken.getAccessToken(), iamUser);
		auditService.log(AuditEventType.LOGIN_SUCCESS, iamUser.getMappedExternalUserId(), null, null);
		return accessToken;
	}

}
