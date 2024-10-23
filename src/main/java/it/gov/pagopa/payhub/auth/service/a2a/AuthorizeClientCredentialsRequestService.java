package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthorizeClientCredentialsRequestService {
	private static final String ERROR = "Unauthorized client for client-credentials";
	private static final String REGEX = "^(\\w+\\s*)(piattaforma-unitaria\\b)$";
	private final ClientService clientService;
	private final ClientMapper clientMapper;
	private final String clientSecretEnv;

	public AuthorizeClientCredentialsRequestService(
			@Value("${piattaforma-unitaria-client-secret}") String clientSecretEnv,
			ClientService clientService,
			ClientMapper clientMapper) {
		this.clientService = clientService;
		this.clientMapper = clientMapper;
		this.clientSecretEnv = clientSecretEnv;
	}

	public ClientDTO authorizeCredentials(String clientId, String clientSecret) {
		Matcher matcher = Pattern.compile(REGEX).matcher(clientId);
		if (matcher.matches()) {
			return retrieveByEnvProperties(clientId, matcher.group(1), matcher.group(2), clientSecret);
		}
		return retrieveByCollection(clientId, clientSecret);
	}

	private ClientDTO retrieveByCollection(String clientId, String clientSecret) {
		return clientService.getClientByClientId(clientId)
			.map(clientMapper::mapToDTO)
			.filter(dto -> dto.getClientSecret().equals(clientSecret))
			.orElseThrow(() -> new ClientUnauthorizedException(ERROR));
	}

	private ClientDTO retrieveByEnvProperties(String clientId, String organizationIpaCode, String clientName, String clientSecret) {
		if (!clientSecret.equals(clientSecretEnv))
			throw new ClientUnauthorizedException(ERROR);
		return ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(clientSecret)
			.build();
	}
}
