package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthorizeClientCredentialsRequestServiceTest {

	@Mock
	private ClientService clientServiceMock;
	@Mock
	private ClientMapper clientMapperMock;
	private AuthorizeClientCredentialsRequestService service;

	@BeforeEach
	void init() {
		service = new AuthorizeClientCredentialsRequestService(clientServiceMock, clientMapperMock, "SECRET");
	}

	@Test
	void givenRightCredentialsWhenAuthorizeCredentialsThenOk() {
		// Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName = "SERVICE_001";
		String clientId = organizationIpaCode + clientName;
		String clientSecretMock = UUID.randomUUID().toString();

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(clientSecretMock)
			.build();

		Mockito.when(clientServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(mockClient));
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);

		// When
		ClientDTO actualClientDTO = service.authorizeCredentials(clientId, clientSecretMock);
		// Then
		Assertions.assertEquals(expectedClientDTO, actualClientDTO);
	}

	@Test
	void givenUnexpectedClientIdCredentialsWhenAuthorizeCredentialsThenClientUnauthorizedException() {
		// Given
		String clientId = "UNEXPECTED_CLIENT_ID";
		String clientSecretMock = UUID.randomUUID().toString();

		Mockito.when(clientServiceMock.getClientByClientId(clientId)).thenThrow(new ClientUnauthorizedException("error"));
		// When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, clientSecretMock));
	}

	@Test
	void givenUnexpectedClientSecretCredentialsWhenAuthorizeCredentialsThenClientUnauthorizedException() {
		// Given
		String organizationIpaCode = "IPA_TEST_2";
		String clientName = "SERVICE_001";
		String clientId = organizationIpaCode + clientName;
		String clientSecret = UUID.randomUUID().toString();

		Client mockClient = new Client();
		ClientDTO expectedClientDTO = ClientDTO.builder()
			.clientId(clientId)
			.clientName(clientName)
			.organizationIpaCode(organizationIpaCode)
			.clientSecret(UUID.randomUUID().toString())
			.build();

		Mockito.when(clientServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(mockClient));
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);

		// When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, clientSecret));
	}

	@Test
	void givenSystemUserWhenMatcherThenAssertionOk() {
		// Given
		String clientId = "piattaforma-unitaria_IPA_TEST";
		String clientSecret = "SECRET";
		String piattaformaUnitaria = "piattaforma-unitaria";
		String separator = "_";
		// When
		ClientDTO actualClientDTO = service.authorizeCredentials(clientId, clientSecret);
		// Then
		Assertions.assertEquals(
		ClientDTO.builder()
			.clientId(clientId)
			.clientName(piattaformaUnitaria)
			.organizationIpaCode(StringUtils.substringAfter(clientId, piattaformaUnitaria + separator))
			.clientSecret(clientSecret)
			.build(), actualClientDTO);
	}

	@Test
	void givenSystemUserWhenMatcherThenClientUnauthorizedException() {
		// Given, When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class,
			() -> service.authorizeCredentials("piattaforma-unitaria_IPA_TEST", "UNEXPECTED_SECRET"));
	}
}
