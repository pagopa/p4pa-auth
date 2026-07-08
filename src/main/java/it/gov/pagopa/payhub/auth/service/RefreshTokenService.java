package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.dto.generated.AccessToken;

public interface RefreshTokenService {
    AccessToken refreshToken(String clientId, String refreshToken);
}
