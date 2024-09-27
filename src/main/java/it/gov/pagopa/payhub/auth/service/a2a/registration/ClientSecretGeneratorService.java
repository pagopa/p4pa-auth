package it.gov.pagopa.payhub.auth.service.a2a.registration;

import it.gov.pagopa.payhub.auth.service.DataCipherService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.function.Function;

@Service
public class ClientSecretGeneratorService implements Function<String, String> {

	private final DataCipherService dataCipherService;

	public ClientSecretGeneratorService(DataCipherService dataCipherService) {
		this.dataCipherService = dataCipherService;
	}

	@Override
	public String apply(String clientSecret) {
		return Base64.getUrlEncoder().encodeToString(dataCipherService.hash(clientSecret));
	}
}
