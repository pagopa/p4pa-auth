package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthorizeClientCredentialsRequestService {
	private static final String PIATTAFORMA_UNITARIA_CLIENT_ID_PREFIX = "piattaforma-unitaria_";
	private final ClientService clientService;
	private final ClientMapper clientMapper;
	private final String piattaformaUnitariaClientSecret;

	public AuthorizeClientCredentialsRequestService(
			ClientService clientService,
			ClientMapper clientMapper,
			@Value("${m2m.piattaforma-unitaria-client-secret}") String piattaformaUnitariaClientSecret) {
		this.clientService = clientService;
		this.clientMapper = clientMapper;
		this.piattaformaUnitariaClientSecret = piattaformaUnitariaClientSecret;
	}

	public ClientDTO authorizeCredentials(String clientId, String clientSecret) {
		if (clientId.startsWith(PIATTAFORMA_UNITARIA_CLIENT_ID_PREFIX)) {
			return authorizePiattaformaUnitariaCredentials(clientId, clientSecret);
		}
		return authorizeSilCredentials(clientId, clientSecret);
	}

	private ClientDTO authorizeSilCredentials(String clientId, String clientSecret) {
		return clientService.getClientByClientId(clientId)
			.map(clientMapper::mapToDTO)
			.filter(dto -> dto.getClientSecret().equals(clientSecret))
			.orElseThrow(() -> new ClientUnauthorizedException("Unauthorized client with client-credentials grant type"));
	}

	private ClientDTO authorizePiattaformaUnitariaCredentials(String clientId, String clientSecret) {
		if (!clientSecret.equals(piattaformaUnitariaClientSecret))
			throw new ClientUnauthorizedException("Unauthorized client for piattaforma-unitaria client-credentials");
		String[] splittedClientId = clientId.split("_");
		return ClientDTO.builder()
			.clientId(clientId)
			.clientName(splittedClientId[0])
			.organizationIpaCode(splittedClientId[1])
			.clientSecret(clientSecret)
			.build();
	}
}
