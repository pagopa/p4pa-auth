package it.gov.pagopa.payhub.auth.service.a2a.revoke;

import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientRemovalServiceTest {

	@Mock
	private ClientRepository clientRepository;
	@InjectMocks
	private ClientRemovalService service;

	@Test
	void givenClientIdWhenRevokeClientThenVerifyRevoke() {
		// Given
		String organizationIpaCode = "organizationIpaCode";
		String clientId = "clientId";
		//When
		service.revokeClient(organizationIpaCode, clientId);
		//Then
		Mockito.verify(clientRepository).deleteClient(organizationIpaCode, clientId);
	}
}
