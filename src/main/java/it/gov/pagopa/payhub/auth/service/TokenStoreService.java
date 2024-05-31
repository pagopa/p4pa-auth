package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.model.generated.AccessToken;

import java.util.Map;

public interface TokenStoreService {
    Map<String, String> save(AccessToken accessToken, Map<String, String> idTokenClaims);
    Map<String, String> load(AccessToken accessToken);
    void delete(AccessToken accessToken);
}
