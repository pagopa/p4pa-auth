package it.gov.pagopa.payhub.auth.service.logout;

import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {

    private final ValidateExternalTokenService validateExternalTokenService;
    private final TokenStoreService tokenStoreService;
    private final AuditLoggerService auditService;

    public LogoutServiceImpl(ValidateExternalTokenService validateExternalTokenService, TokenStoreService tokenStoreService,
        AuditLoggerService auditService) {
        this.validateExternalTokenService = validateExternalTokenService;
        this.tokenStoreService = tokenStoreService;
        this.auditService = auditService;
    }

    @Override
    public void logout(String clientId, String token) {
        validateExternalTokenService.validateClient(clientId);
        tokenStoreService.delete(token);
        auditService.log(AuditEventType.LOGOUT, clientId, null, "Logout");
    }
}
