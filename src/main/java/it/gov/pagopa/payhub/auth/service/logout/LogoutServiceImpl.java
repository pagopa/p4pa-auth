package it.gov.pagopa.payhub.auth.service.logout;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.gov.pagopa.payhub.auth.enums.AuditEventType;
import it.gov.pagopa.payhub.auth.service.AuditLoggerService;
import it.gov.pagopa.payhub.auth.service.TokenStoreService;
import it.gov.pagopa.payhub.auth.service.exchange.ValidateExternalTokenService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService.ACCESS_TOKEN_TYPE;
import static it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService.REFRESH_TOKEN_TYPE;

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
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        String tokenType = claims.get("typ").asString();
        if(REFRESH_TOKEN_TYPE.equals(tokenType)) {
            tokenStoreService.deleteRefreshToken(token);
            auditService.log(AuditEventType.LOGOUT, null, "Logout - access token");
        } else if(ACCESS_TOKEN_TYPE.equals(tokenType)) {
            tokenStoreService.delete(token);
            auditService.log(AuditEventType.LOGOUT, null, "Logout - session");
        }
    }
}
