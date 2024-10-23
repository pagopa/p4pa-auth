package it.gov.pagopa.payhub.auth.service.a2a;

import it.gov.pagopa.payhub.auth.exception.custom.ClientUnauthorizedException;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.Client;
import it.gov.pagopa.payhub.model.generated.ClientDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
class AuthorizeClientCredentialsRequestServiceTest {

	@Mock
	private ClientService clientServiceMock;
	@Mock
	private ClientMapper clientMapperMock;
	private AuthorizeClientCredentialsRequestService service;

	private static final String REGEX = "^(\\w+\\s*)(piattaforma-unitaria\\b)$";

	@BeforeEach
	void init() {
		service = new AuthorizeClientCredentialsRequestService("SECRET", clientServiceMock, clientMapperMock);
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
		Assertions.assertFalse(Pattern.compile(REGEX).matcher(clientId).matches());
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

		Assertions.assertFalse(Pattern.compile(REGEX).matcher(clientId).matches());
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

		Assertions.assertFalse(Pattern.compile(REGEX).matcher(clientId).matches());
		Mockito.when(clientServiceMock.getClientByClientId(clientId)).thenReturn(Optional.of(mockClient));
		Mockito.when(clientMapperMock.mapToDTO(mockClient)).thenReturn(expectedClientDTO);

		// When, Then
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, clientSecret));
	}

	@Test
	void givenSystemUserWhenMatcherThenAssertionOk() {
		// Given
		String clientId = "IPA_TEST_2piattaforma-unitaria";
		String clientSecretEnv = "SECRET";
		Matcher matcher = Pattern.compile(REGEX).matcher(clientId);

		// When
		ClientDTO actualClientDTO = service.authorizeCredentials(clientId, clientSecretEnv);
		Assertions.assertTrue(matcher.matches());
		// Then
		Assertions.assertEquals(
			ClientDTO.builder()
				.clientId(clientId)
				.organizationIpaCode(matcher.group(1))
				.clientName(matcher.group(2))
				.clientSecret(clientSecretEnv)
				.build()
			, actualClientDTO);
	}

	@Test
	void givenSystemUserWhenMatcherThenClientUnauthorizedException() {
		// Given
		String clientId = "IPA_TEST_2piattaforma-unitaria";
		Matcher matcher = Pattern.compile(REGEX).matcher(clientId);

		// When, Then
		Assertions.assertTrue(matcher.matches());
		Assertions.assertThrows(ClientUnauthorizedException.class, () -> service.authorizeCredentials(clientId, "UNEXPECTED_SECRET"));
	}
}
