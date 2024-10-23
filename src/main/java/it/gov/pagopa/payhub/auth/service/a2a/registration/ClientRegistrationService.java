package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.exception.custom.M2MClientConflictException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ClientRegistrationService {

	private final ClientMapper clientMapper;
	private final ClientRepository clientRepository;

	public ClientRegistrationService(ClientMapper clientMapper, ClientRepository clientRepository) {
		this.clientMapper = clientMapper;
		this.clientRepository = clientRepository;
	}

	public Client registerClient(String clientName, String organizationIpaCode) {

		Client client = clientMapper.mapToModel(
			ClientDTO.builder()
				.clientId(organizationIpaCode + clientName)
				.clientName(clientName)
				.organizationIpaCode(organizationIpaCode)
				.clientSecret(UUID.randomUUID().toString())
				.build()
		);
		log.info("Registering client having clientName {} and organizationIpaCode {}", clientName, organizationIpaCode);
		try{
			return clientRepository.insert(client);
		} catch (DuplicateKeyException e){
			throw new M2MClientConflictException("Client with name " + clientName + " already exists under organization " + organizationIpaCode);
		}
	}
}
