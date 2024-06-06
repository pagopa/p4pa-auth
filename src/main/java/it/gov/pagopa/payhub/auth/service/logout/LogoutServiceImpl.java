package it.gov.pagopa.payhub.auth.service.logout;

import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {

    private final ValidateExternalTokenService validateExternalTokenService;
    private final TokenStoreService tokenStoreService;

    public LogoutServiceImpl(ValidateExternalTokenService validateExternalTokenService, TokenStoreService tokenStoreService) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.tokenStoreService = tokenStoreService;
    }

    @Override
    public void logout(String clientId, String token) {
        validateExternalTokenService.validateClient(clientId);
        tokenStoreService.delete(token);
    }
}
