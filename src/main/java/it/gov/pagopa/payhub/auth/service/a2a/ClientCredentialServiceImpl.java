package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.mapper.ClientDTO2UserInfoMapper;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.model.generated.AccessToken;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientCredentialServiceImpl implements ClientCredentialService {

	private final ValidateClientCredentialsService validateClientCredentialsService;
	private final AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService;
	private final AccessTokenBuilderService accessTokenBuilderService;
	private final TokenStoreService tokenStoreService;
	private final ClientDTO2UserInfoMapper clientDTO2UserInfoMapper;

	public ClientCredentialServiceImpl(
			ValidateClientCredentialsService validateClientCredentialsService,
			AuthorizeClientCredentialsRequestService authorizeClientCredentialsRequestService,
			AccessTokenBuilderService accessTokenBuilderService,
			TokenStoreService tokenStoreService, ClientDTO2UserInfoMapper clientDTO2UserInfoMapper) {
		this.validateClientCredentialsService = validateClientCredentialsService;
		this.authorizeClientCredentialsRequestService = authorizeClientCredentialsRequestService;
		this.accessTokenBuilderService = accessTokenBuilderService;
		this.tokenStoreService = tokenStoreService;
		this.clientDTO2UserInfoMapper = clientDTO2UserInfoMapper;
	}

	@Override
	public AccessToken postToken(String clientId, String scope, String clientSecret) {
		log.info("Client {} requested authentication with client_credentials grant type and scope {}", clientId, scope);
		validateClientCredentialsService.validate(scope, clientSecret);
		ClientDTO authorizedClient = authorizeClientCredentialsRequestService.authorizeCredentials(clientId, clientSecret);
		AccessToken accessToken = accessTokenBuilderService.build();
		IamUserInfoDTO iamUser = clientDTO2UserInfoMapper.apply(authorizedClient);
		tokenStoreService.save(accessToken.getAccessToken(), iamUser);
		return accessToken;
	}
}
