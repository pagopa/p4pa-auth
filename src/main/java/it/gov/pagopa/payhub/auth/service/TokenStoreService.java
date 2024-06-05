package it.gov.pagopa.payhub.auth.service;

import java.util.Map;

public interface TokenStoreService {
    Map<String, String> save(String accessToken, Map<String, String> idTokenClaims);
    Map<String, String> load(String accessToken);
    void delete(String accessToken);
}
